# Dokumentasi Project Split Bill

## Menjawab pertanyaan

```java
"What was the hardest design decision you made while building this, and what trade-off did you accept?"
```

ketika membangun project ini,
keputusan desain tersulit adalah bagaimana menangani logika split bill yang fleksibel
namun tetap sederhana di waktu yang terbatas.
Saya memutuskan untuk menggunakan strategi split bill berbasis metode (`bagirata`, `percentage`, `exact`) yang dapat diperluas di masa depan.
Trade-off yang diterima adalah kompleksitas tambahan dalam service layer untuk menghitung pembagian tagihan, validasi logika,
dan perhitungan biaya layanan.
Saya memilih fleksibilitas dan skalabilitas di atas kesederhanaan implementasi awal,
sehingga memungkinkan penambahan strategi split bill baru tanpa mengubah struktur database atau model utama.
masih banyak yang perlu saya perbaiki, seperti validasi dan me-refaktor kode agar lebih modular.


## 1. Gambaran Umum

Split Bill adalah project API backend berbasis Spring Boot untuk mengelola pembagian tagihan bersama antar pengguna. Aplikasi ini memungkinkan pengguna membuat transaksi, menentukan cara pembagian tagihan, menghitung biaya layanan, memantau saldo, dan menandai status pembayaran.

Project ini dibangun sebagai backend service dengan dukungan database MySQL dan menggunakan Spring MVC serta JPA untuk operasi data.

## 2. Teknologi yang Digunakan

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Spring Security (BCrypt password encoder)
- MySQL
- Lombok
- Maven

## 3. Tujuan Project

Aplikasi ini mendukung beberapa kebutuhan seperti:

- registrasi user
- menampilkan data user
- membuat transaksi pembagian tagihan
- membagi tagihan secara rata, persentase, atau nominal tertentu
- menghitung biaya layanan dan total pembayaran
- mengecek apakah saldo user cukup
- menandai transaksi sebagai `PAID` atau `UNPAID`
- menampilkan ringkasan settlement per user dan per kategori

## 4. Arsitektur Project

Project ini menggunakan pola layered yang umum, yaitu:

- Controller layer: menangani request HTTP
- Service layer: berisi logika bisnis
- Repository layer: operasi database menggunakan JPA repository
- Model layer: entity yang dipetakan ke tabel database
- DTO layer: objek request/response

Struktur package utama:

- `com.syaiful.split.controller`
- `com.syaiful.split.service`
- `com.syaiful.split.repository`
- `com.syaiful.split.model`
- `com.syaiful.split.dto`
- `com.syaiful.split.config`

## 5. Entitas Utama

### Persons

Entity `Persons` dipetakan ke tabel `persons`.

Field utama:

- `id`
- `firstName`
- `lastName`
- `noTlp`
- `amount`
- `email`
- `password`
- `role`

Entity ini menyimpan data pengguna dan saldo saat ini (`amount`).

### Transactions

Entity `Transactions` dipetakan ke tabel `transaksi`.

Field utama:

- `id`
- `namaTransaksi`
- `splitBillCode`
- `splitBillMethod`
- `isSplitBill`
- `splitBillCreatorType`
- `statusPembayaran`
- `kategoriPengeluaran`
- `dibuatOleh`
- `waktuDibuat`
- `dibayarOleh`
- `waktuPembayaran`
- `amount`
- `serviceChargePct`
- `serviceChargeAmount`
- `totalBayar`

## 6. Logika Bisnis Utama

### Metode pembagian tagihan

Aplikasi mendukung beberapa strategi split bill, yaitu:

- `bagirata` → pembagian rata
- `percentage` → pembagian berdasarkan persentase
- `exact` → pembagian berdasarkan nominal tertentu

### Logika biaya layanan

Biaya layanan dihitung berdasarkan persentase yang diturunkan dari jumlah Unicode pada string `syaiful-scooter`.

Pseudo logika:

- `sumUnicode("syaiful-scooter")`
- `serviceChargePct = (sum % 10) / 100`
- `serviceChargeAmount = amount * feePercent`
- `totalBayar = amount + serviceChargeAmount`

### Validasi saldo

Saat creator membayar tagihan, aplikasi memeriksa apakah saldo user cukup sebelum transaksi diproses:

- jika `saldo >= totalBayar` → valid
- jika tidak → ditolak dengan pesan `Saldo tidak mencukupi`

## 7. Endpoint API

### 7.1 Registrasi user

- Method: `POST`
- Endpoint: `/api/auth/register`

Contoh request body:

```json
{
  "firstName": "Syaiful",
  "lastName": "Scooter",
  "noTlp": "081234567890",
  "amount": 5000000,
  "email": "syaiful@example.com",
  "password": "secret123",
  "role": "USER"
}
```

### 7.2 Menampilkan data user

- Method: `GET`
- Endpoint: `/master/person/`

Contoh response:

```json
[
  {
    "id": 1,
    "firstName": "Syaiful",
    "lastName": "Scooter"
  }
]
```

### 7.3 Membuat transaksi split bill

- Method: `POST`
- Endpoint: `/simpan/`

Contoh request body:

```json
{
  "amount": 150000,
  "namaTransaksi": "Makan Siang",
  "isSplitBill": "true",
  "kategoriPengeluaran": "Food",
  "splitStrategy": "bagirata",
  "datarow": [
    {
      "namaTransaksi": "Makan Siang",
      "isSplitBill": true,
      "splitBillCreatorType": "creator",
      "statusPembayaran": "PAID",
      "kategoriPengeluaran": "Food",
      "dibuatOleh": 1,
      "dibayarOleh": 1,
      "amount": 150000
    },
    {
      "namaTransaksi": "Makan Siang",
      "isSplitBill": true,
      "splitBillCreatorType": "member",
      "statusPembayaran": "UNPAID",
      "kategoriPengeluaran": "Food",
      "dibuatOleh": 1,
      "dibayarOleh": 2,
      "amount": 150000
    }
  ]
}
```

Contoh response:

```json
{
  "status": true,
  "message": "Menyimpan dengan pembagian secara merata, Sukses dilakukan",
  "data": [
    {
      "id": 10,
      "namaTransaksi": "Makan Siang",
      "statusPembayaran": "PAID"
    }
  ]
}
```

### 7.4 Membayar tagihan

- Method: `PUT`
- Endpoint: `/bayar`

Contoh request body:

```json
{
  "dibayarOleh": 2,
  "splitBillCode": "Makan Siang - 3b4d1e90-..."
}
```

Contoh response berhasil:

```json
{
  "status": true,
  "message": "Pembayaran Sukses"
}
```

Contoh response gagal karena saldo tidak cukup:

```json
{
  "status": false,
  "message": "Saldo tidak mencukupi"
}
```

### 7.5 Ringkasan settlement

- Method: `GET`
- Endpoint: `/settlement/summary/{userId}`

Contoh:

```http
GET /settlement/summary/1
```

- Method: `GET`
- Endpoint: `/settlement/totalkategori/{userId}`

Contoh:

```http
GET /settlement/totalkategori/1
```

- Method: `GET`
- Endpoint: `/settlement/summary/{userId}/{kategoriPengeluaran}`

Contoh:

```http
GET /settlement/summary/1/Food
```

## 8. Konfigurasi Database

Project saat ini menggunakan konfigurasi berikut di `src/main/resources/application.properties`:

```properties
spring.application.name=split.bill
server.port=4110

spring.datasource.url=jdbc:mysql://localhost:3306/allo_split_bill
spring.datasource.username=root
spring.datasource.password=

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

Pastikan MySQL sudah aktif dan database `allo_split_bill` sudah tersedia sebelum menjalankan aplikasi.

## 9. Cara Menjalankan Project

### Prasyarat

- Java 17+
- Maven
- MySQL Server

### Menjalankan dengan Maven

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Aplikasi akan berjalan di:

```text
http://localhost:4110
```

## 10. Catatan dan Status Saat Ini

Project ini masih dalam tahap prototype backend dan lebih fokus pada logika bisnis dibandingkan dengan keamanan dan standardisasi API yang full production. Keunggulan utama project ini adalah:

- perhitungan split bill
- validasi saldo
- ringkasan settlement pembayaran
- penyimpanan data dengan MySQL dan JPA

Beberapa peningkatan yang bisa dilakukan ke depannya:

- autentikasi JWT atau session-based
- konfigurasi security untuk endpoint yang perlu dilindungi
- validasi input dan exception handling yang lebih konsisten
- unit test dan integration test
- transaction rollback dan audit log

## 11. Kesimpulan

Split Bill adalah aplikasi backend yang dirancang untuk mengelola pembayaran bersama antar pengguna. Project ini menjadi fondasi yang praktis untuk menangani pembuatan transaksi, logika split, perhitungan biaya layanan, serta tracking settlement pembayaran dengan Java dan Spring Boot.



