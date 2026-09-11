package com.syaiful.split.service;

import com.syaiful.split.dto.*;
import com.syaiful.split.model.Persons;
import com.syaiful.split.model.Transactions;
import com.syaiful.split.repository.PersonsRepo;
import com.syaiful.split.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo repo;

    @Autowired
    private PersonsRepo personsRepo;

    public Transactions save(PembayaranBagiRataDto bayar) {

        Transactions trx = new Transactions();

        trx.setDibuatOleh(bayar.getDibuatOleh());
        trx.setNamaTransaksi(bayar.getNamaTransaksi());
        trx.setIsSplitBill(bayar.getIsSplitBill());
        trx.setSplitBillCreatorType(bayar.getSplitBillCreatorType());
        trx.setStatusPembayaran(bayar.getStatusPembayaran());
        trx.setKategoriPengeluaran(bayar.getKategoriPengeluaran());
        trx.setWaktuDibuat(bayar.getWaktuDibuat());
        trx.setWaktuPembayaran(bayar.getWaktuPembayaran());
        trx.setAmount(bayar.getAmount());
// Tampilkan isi data ke console
        System.out.println("===== DATA TRANSAKSI =====");
        System.out.println("Dibuat Oleh          : " + trx.getDibuatOleh());
        System.out.println("Nama Transaksi       : " + trx.getNamaTransaksi());
        System.out.println("Is Split Bill        : " + trx.getIsSplitBill());
        System.out.println("Creator Type         : " + trx.getSplitBillCreatorType());
        System.out.println("Status Pembayaran    : " + trx.getStatusPembayaran());
        System.out.println("Kategori Pengeluaran : " + trx.getKategoriPengeluaran());
        System.out.println("Waktu Dibuat         : " + trx.getWaktuDibuat());
        System.out.println("Waktu Pembayaran     : " + trx.getWaktuPembayaran());
        System.out.println("Amount               : " + trx.getAmount());
        System.out.println("==========================");

        // Sementara jangan simpan ke database
        // return repo.save(trx);

        return trx;
    }

    @Transactional
    public List<Transactions> saveBagiRata(List<PembayaranBagiRataDto> request, BigDecimal Amount) {
        String GitName = "syaiful-scooter";
        String SplitBillMethod = "Bagi Rata";
        String kodeUnik = UUID.randomUUID().toString();
        int b = sumUnicode(GitName);

        BigDecimal service_charge_pct = (BigDecimal.valueOf((b % 10)).divide(BigDecimal.valueOf(100)));

        BigDecimal serviceChargeAmount = Amount.multiply(service_charge_pct);

        BigDecimal totalBayar = Amount.add(serviceChargeAmount);

        List<Transactions> transactions = new ArrayList<>();

        for (PembayaranBagiRataDto bayar : request) {

            Transactions trx = new Transactions();

            trx.setNamaTransaksi(bayar.getNamaTransaksi());

            trx.setSplitBillCode(bayar.getNamaTransaksi().toString().concat(" - ").concat(kodeUnik));

            trx.setSplitBillMethod(SplitBillMethod);

            trx.setIsSplitBill(true);

            trx.setSplitBillCreatorType(bayar.getSplitBillCreatorType());

            trx.setAmount(Amount);

            trx.setServiceChargePct(service_charge_pct);

            trx.setServiceChargeAmount(serviceChargeAmount);

            trx.setTotalBayar(totalBayar);

            trx.setStatusPembayaran(bayar.getStatusPembayaran()); //cek dulu kalau creator maka paid

            trx.setWaktuPembayaran(LocalDateTime.now()); // cek dulu kalau member maka null

            trx.setKategoriPengeluaran(bayar.getKategoriPengeluaran());

            trx.setDibuatOleh(bayar.getDibuatOleh());

            trx.setDibayarOleh(bayar.getDibayarOleh());

            trx.setWaktuDibuat(LocalDateTime.now());

            Transactions saved = repo.save(trx);

            transactions.add(saved);
        }
        return transactions;
    }

    @Transactional
    public ArrayList<Transactions> create(PembayaranBagiRataRequestDto request) {
        if (request == null || request.getDatarow() == null || request.getDatarow().isEmpty()) {
            return new ArrayList<>();
        }

        String gitName = "syaiful-scooter";
        int sumGitName = sumUnicode(gitName);
        boolean cukupSaldo = false;

        //cek saldo orang yang simpan transaksi, apakah cukup untuk membayar tagihan
        //kalau tidak cukup, maka tidak bisa menyimpan transaksi
        for (PembayaranBagiRataDto bayar : request.getDatarow()) {
            if (bayar == null) {
                continue;
            }

            if("creator".equals(bayar.getSplitBillCreatorType())) {

                Persons p = getPerson(bayar.getDibuatOleh() );
                BigDecimal saldoPerson = p.getAmount();
                BigDecimal totalAmount = request.getAmount().add(request.getAmount().multiply(BigDecimal.valueOf(sumGitName % 10).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));

                //tampilkan saldo dan total amount ke console
                System.out.println("Saldo Person: " + saldoPerson);
                System.out.println("Total Amount: " + totalAmount);
                cukupSaldo = totalAmount.compareTo(saldoPerson) <= 0;
            }

//            if (!cukupSaldo) {
//                throw new RuntimeException("Saldo tidak mencukupi untuk membayar tagihan");
//            }
        }

        BigDecimal totalAmount = request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO;
        int jumlahOrang = request.getDatarow().size();

        String splitBillMethod = request.getSplitStrategy();

        BigDecimal serviceChargePct = BigDecimal.valueOf(sumGitName % 10).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        String kodeUnik = UUID.randomUUID().toString();

        ArrayList<Transactions> transactions = new ArrayList<>();

        if ("bagirata".equals(splitBillMethod))
        {
            BigDecimal amountPerPerson = totalAmount.divide(BigDecimal.valueOf(jumlahOrang), 2, RoundingMode.HALF_UP);
            BigDecimal serviceChargeAmount = amountPerPerson.multiply(serviceChargePct).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalBayar = amountPerPerson.add(serviceChargeAmount);
            BigDecimal totalBayarCreator = totalAmount.add(totalAmount.multiply(serviceChargePct)).setScale(2, RoundingMode.HALF_UP);

            for (PembayaranBagiRataDto bayar : request.getDatarow()) {
                if (bayar == null) {
                    continue;
                }

                Transactions trx = new Transactions();

                if("creator".equals(bayar.getSplitBillCreatorType())) {
                    Persons p = getPerson(bayar.getDibayarOleh());
                    BigDecimal saldoPerson = p.getAmount();

                    trx.setAmount(request.getAmount());
                    trx.setTotalBayar(totalBayarCreator);
                    trx.setServiceChargeAmount(request.getAmount().multiply(serviceChargePct).setScale(2, RoundingMode.HALF_UP));

                    //proses bayar tagihan untuk creator
                    BigDecimal newSaldo = saldoPerson.subtract(totalBayarCreator);
                    p.setAmount(newSaldo);

                    trx.setStatusPembayaran("PAID");
                    trx.setWaktuPembayaran(LocalDateTime.now());

                    personsRepo.save(p);
                }else{
                    trx.setAmount(amountPerPerson);
                    trx.setTotalBayar(totalBayar);
                    trx.setStatusPembayaran("UNPAID");
                    trx.setServiceChargeAmount(serviceChargeAmount);
                }

                String namaTransaksi = bayar.getNamaTransaksi() != null ? bayar.getNamaTransaksi() : request.getNamaTransaksi();

                trx.setNamaTransaksi(namaTransaksi);
                trx.setSplitBillCode(namaTransaksi + " - " + kodeUnik);
                trx.setSplitBillMethod(splitBillMethod);
                trx.setIsSplitBill(Boolean.TRUE.equals(bayar.getIsSplitBill()) || "true".equalsIgnoreCase(request.getIsSplitBill()));
                trx.setSplitBillCreatorType(bayar.getSplitBillCreatorType());

                trx.setServiceChargePct(serviceChargePct);


                trx.setKategoriPengeluaran(bayar.getKategoriPengeluaran() != null ? bayar.getKategoriPengeluaran() : request.getKategoriPengeluaran());
                trx.setDibuatOleh(bayar.getDibuatOleh());
                trx.setDibayarOleh(bayar.getDibayarOleh());
                trx.setWaktuDibuat(LocalDateTime.now());

                Transactions saved = repo.save(trx);
                transactions.add(saved);
            }
            return transactions;
        }
        else if ("percentage".equals(splitBillMethod))
        {
            BigDecimal totalProsentase = BigDecimal.ZERO;
            for (PembayaranBagiRataDto bayar : request.getDatarow()) {
                if (bayar != null && bayar.getProsentase() != null) {
                    totalProsentase = totalProsentase.add(bayar.getProsentase());
                }
            }
            if (totalProsentase.compareTo(BigDecimal.valueOf(100)) != 0) {
                throw new IllegalArgumentException("Total persentase belum 100 %");
            }

            if (totalProsentase.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Total persentase tidak valid untuk split percentage");
            }

            for (PembayaranBagiRataDto bayar : request.getDatarow()) {
                if (bayar == null) {
                    continue;
                }

                BigDecimal prosentase = bayar.getProsentase() != null ? bayar.getProsentase() : BigDecimal.ZERO;
                BigDecimal amountPerPerson = totalAmount.multiply(prosentase).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                BigDecimal serviceChargeAmount = amountPerPerson.multiply(serviceChargePct).setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalBayar = amountPerPerson.add(serviceChargeAmount);

                BigDecimal totalBayarCreator = totalAmount.add(totalAmount.multiply(serviceChargePct)).setScale(2, RoundingMode.HALF_UP);

                Transactions trx = new Transactions();

                if("creator".equals(bayar.getSplitBillCreatorType())) {
                    Persons p = getPerson(bayar.getDibayarOleh());
                    BigDecimal saldoPerson = p.getAmount();

                    trx.setAmount(request.getAmount());
                    trx.setTotalBayar(totalBayarCreator);
                    trx.setServiceChargeAmount(request.getAmount().multiply(serviceChargePct).setScale(2, RoundingMode.HALF_UP));

                    //proses bayar tagihan untuk creator
                    BigDecimal newSaldo = saldoPerson.subtract(totalBayarCreator);
                    p.setAmount(newSaldo);

                    trx.setStatusPembayaran("PAID");
                    trx.setWaktuPembayaran(LocalDateTime.now());

                    personsRepo.save(p);
                }else{
                    trx.setAmount(amountPerPerson);
                    trx.setTotalBayar(totalBayar);
                    trx.setStatusPembayaran("UNPAID");
                    trx.setServiceChargeAmount(serviceChargeAmount);
                }

                String namaTransaksi = bayar.getNamaTransaksi() != null ? bayar.getNamaTransaksi() : request.getNamaTransaksi();

                trx.setNamaTransaksi(namaTransaksi);
                trx.setSplitBillCode(namaTransaksi + " - " + kodeUnik);
                trx.setSplitBillMethod(splitBillMethod);
                trx.setIsSplitBill(Boolean.TRUE.equals(bayar.getIsSplitBill()) || "true".equalsIgnoreCase(request.getIsSplitBill()));
                trx.setSplitBillCreatorType(bayar.getSplitBillCreatorType());
                trx.setServiceChargePct(serviceChargePct);

                trx.setKategoriPengeluaran(bayar.getKategoriPengeluaran() != null ? bayar.getKategoriPengeluaran() : request.getKategoriPengeluaran());
                trx.setDibuatOleh(bayar.getDibuatOleh());
                trx.setDibayarOleh(bayar.getDibayarOleh());
                trx.setWaktuDibuat(LocalDateTime.now());

                Transactions saved = repo.save(trx);
                transactions.add(saved);
            }
            return transactions;
        }
        else if ("exact".equals(splitBillMethod))
        {
            BigDecimal totalAmountPatungan = BigDecimal.ZERO;
            for (PembayaranBagiRataDto bayar : request.getDatarow()) {
                if (bayar != null && bayar.getAmount() != null) {
                    totalAmountPatungan = totalAmountPatungan.add(bayar.getAmount());
                }
            }
            if (request.getAmount().compareTo(totalAmountPatungan) != 0) {
                throw new IllegalArgumentException("Total patungan belum sama dengan harga bayar");
            }

            for (PembayaranBagiRataDto bayar : request.getDatarow()) {
                if (bayar == null) {
                    continue;
                }

//                BigDecimal prosentase = bayar.getProsentase() != null ? bayar.getProsentase() : BigDecimal.ZERO;
//                BigDecimal amountPerPerson = totalAmount.multiply(prosentase).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal amountPerPerson = bayar.getAmount();

                BigDecimal serviceChargeAmount = amountPerPerson.multiply(serviceChargePct).setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalBayar = amountPerPerson.add(serviceChargeAmount);

                BigDecimal totalBayarCreator = totalAmount.add(totalAmount.multiply(serviceChargePct)).setScale(2, RoundingMode.HALF_UP);

                Transactions trx = new Transactions();

                if("creator".equals(bayar.getSplitBillCreatorType())) {
                    Persons p = getPerson(bayar.getDibayarOleh());
                    BigDecimal saldoPerson = p.getAmount();

                    trx.setAmount(request.getAmount());
                    trx.setTotalBayar(totalBayarCreator);
                    trx.setServiceChargeAmount(request.getAmount().multiply(serviceChargePct).setScale(2, RoundingMode.HALF_UP));

                    //proses bayar tagihan untuk creator
                    BigDecimal newSaldo = saldoPerson.subtract(totalBayarCreator);
                    p.setAmount(newSaldo);

                    trx.setStatusPembayaran("PAID");
                    trx.setWaktuPembayaran(LocalDateTime.now());

                    personsRepo.save(p);
                }else{
                    trx.setAmount(amountPerPerson);
                    trx.setTotalBayar(totalBayar);
                    trx.setStatusPembayaran("UNPAID");
                    trx.setServiceChargeAmount(serviceChargeAmount);
                }

                String namaTransaksi = bayar.getNamaTransaksi() != null ? bayar.getNamaTransaksi() : request.getNamaTransaksi();

                trx.setNamaTransaksi(namaTransaksi);
                trx.setSplitBillCode(namaTransaksi + " - " + kodeUnik);
                trx.setSplitBillMethod(splitBillMethod);
                trx.setIsSplitBill(Boolean.TRUE.equals(bayar.getIsSplitBill()) || "true".equalsIgnoreCase(request.getIsSplitBill()));
                trx.setSplitBillCreatorType(bayar.getSplitBillCreatorType());
                trx.setServiceChargePct(serviceChargePct);

                trx.setKategoriPengeluaran(bayar.getKategoriPengeluaran() != null ? bayar.getKategoriPengeluaran() : request.getKategoriPengeluaran());
                trx.setDibuatOleh(bayar.getDibuatOleh());
                trx.setDibayarOleh(bayar.getDibayarOleh());
                trx.setWaktuDibuat(LocalDateTime.now());

                Transactions saved = repo.save(trx);
                transactions.add(saved);
            }
            return transactions;
        }
        else
        {
            throw new IllegalArgumentException("Split strategy tidak valid: " + request.getSplitStrategy());
        }
    }

    private Persons getPerson(int id) {
        return personsRepo.findById(id).orElse(null);
    }

    private Transactions getTrx(String code, int dibayarOleh) {
        return repo.findBySplitBillCodeAndDibayarOleh(code, dibayarOleh).stream().findFirst().orElse(null);
    }

    public static int sumUnicode(String text) {
        if (text == null) {
            return 0;
        }
        return text.codePoints().sum();
    }

    public Boolean cekSaldo(BayarDto dto) {
        Persons p = getPerson(dto.getDibayarOleh());
        Transactions trx = getTrx(dto.getSplitBillCode(), dto.getDibayarOleh());

        BigDecimal saldo = p.getAmount();

        return saldo.compareTo(trx.getTotalBayar()) > 0;
    }
    public Boolean cekSaldoVsReq(BayarDto dto) {
        Persons p = getPerson(dto.getDibayarOleh());
        Transactions trx = getTrx(dto.getSplitBillCode(), dto.getDibayarOleh());

        BigDecimal saldo = p.getAmount();

        return saldo.compareTo(trx.getTotalBayar()) > 0;
    }

    public Transactions bayarTagihan(BayarDto dto) {
        Persons p = getPerson(dto.getDibayarOleh());
        Transactions trx = getTrx(dto.getSplitBillCode(), dto.getDibayarOleh());

        // Person tidak ditemukan
        if (p == null) {
            throw new RuntimeException("Person tidak ditemukan");
        }

        if ("UNPAID".equals(trx.getStatusPembayaran())) {
            BigDecimal saldo = p.getAmount();
            BigDecimal newSaldo = saldo.subtract(trx.getTotalBayar());

            p.setAmount(newSaldo);
            trx.setStatusPembayaran("PAID");
            trx.setWaktuPembayaran(LocalDateTime.now());

            personsRepo.save(p);
        }

        return trx;
    }

    public List<BayarTagihanDto> bayarTagihanku(BayarDto dto) {
        boolean status = true;
        String message = "Pembayaran Sukses";

        List<BayarTagihanDto> listTrx = new ArrayList<>();

        BayarTagihanDto btDto = new BayarTagihanDto();

        Persons p = getPerson(dto.getDibayarOleh());
        Transactions trx = getTrx(dto.getSplitBillCode(), dto.getDibayarOleh());

        // Person tidak ditemukan
        if (p == null) {
            status = false;
            message = "User tidak ditemukan";

            btDto.setStatus(status);
            btDto.setMessage(message);
            listTrx.add(btDto);

            return listTrx;
        }

        if ("PAID".equals((trx.getStatusPembayaran()))) {
            status = false;
            message = "Tagihan Sudah Lunas";

            btDto.setStatus(status);
            btDto.setMessage(message);
            listTrx.add(btDto);

            return listTrx;
        }

        boolean cukup = cekSaldo(dto);
        if (!cukup) {
            status = false;
            message = "Saldo tidak mencukupi";

            btDto.setStatus(status);
            btDto.setMessage(message);
            listTrx.add(btDto);

            return listTrx;
        }

        BigDecimal saldo = p.getAmount();
        BigDecimal newSaldo = saldo.subtract(trx.getTotalBayar());

        p.setAmount(newSaldo);
        trx.setStatusPembayaran("PAID");
        trx.setWaktuPembayaran(LocalDateTime.now());

        personsRepo.save(p);

        btDto.setStatus(status);
        btDto.setMessage(message);
        listTrx.add(btDto);

        return listTrx;
    }


    //Buat settlement summary per user, untuk menampilkan semua transaksi yang dilakukan oleh user tersebut
    public List<SettlementSumaryDto> getSettlementSummaryByUserId(Integer dibuatOleh) {

        List<Transactions> transactions = repo.findByDibuatOleh(dibuatOleh);

        return transactions.stream().map(trx -> {

            Persons p = getPerson(trx.getDibuatOleh());
            SettlementSumaryDto summary = new SettlementSumaryDto();

            summary.setTransactionId(trx.getId());
            summary.setSplitBillCode(trx.getSplitBillCode());
            summary.setNamaTransaksi(trx.getNamaTransaksi());
            summary.setStatusPembayaran(trx.getStatusPembayaran());
            summary.setKategoriPengeluaran(trx.getKategoriPengeluaran());
            summary.setDibuatOleh(p != null ? p.getFirstName().concat(" ").concat(p.getLastName()) : "Unknown");
            summary.setDibayarOleh(getPerson(trx.getDibayarOleh()) != null ? getPerson(trx.getDibayarOleh()).getFirstName().concat(" ").concat(getPerson(trx.getDibayarOleh()).getLastName()) : "Unknown");
            summary.setTglDibuat(trx.getWaktuDibuat());
            summary.setTglBayar(trx.getWaktuPembayaran());
            summary.setAmount(trx.getAmount());
            summary.setServisFee(trx.getServiceChargeAmount());
            summary.setServisFeePercent(trx.getServiceChargePct());
            summary.setTotalBayar(trx.getTotalBayar());
            summary.setSplitBillCreatorType(trx.getSplitBillCreatorType());

            return summary;

        }).collect(Collectors.toList());

    }

    // Settlement per user dan per kategori
    public List<SettlementSumaryDto> getSettlementSummaryByUserIdAndKategoriPengeluaran(Integer dibuatOleh, String kategoriPengeluaran) {

        List<Transactions> transactions = repo.findByDibuatOlehAndKategoriPengeluaranIgnoreCase(dibuatOleh, kategoriPengeluaran);

        return transactions.stream().map(trx -> {

            Persons p = getPerson(trx.getDibuatOleh());
            SettlementSumaryDto summary = new SettlementSumaryDto();

            summary.setTransactionId(trx.getId());
            summary.setSplitBillCode(trx.getSplitBillCode());
            summary.setNamaTransaksi(trx.getNamaTransaksi());
            summary.setStatusPembayaran(trx.getStatusPembayaran());
            summary.setKategoriPengeluaran(trx.getKategoriPengeluaran());
            summary.setDibuatOleh(p != null ? p.getFirstName().concat(" ").concat(p.getLastName()) : "Unknown");
            summary.setDibayarOleh(getPerson(trx.getDibayarOleh()) != null ? getPerson(trx.getDibayarOleh()).getFirstName().concat(" ").concat(getPerson(trx.getDibayarOleh()).getLastName()) : "Unknown");
            summary.setTglDibuat(trx.getWaktuDibuat());
            summary.setTglBayar(trx.getWaktuPembayaran());
            summary.setAmount(trx.getAmount());
            summary.setServisFee(trx.getServiceChargeAmount());
            summary.setServisFeePercent(trx.getServiceChargePct());
            summary.setTotalBayar(trx.getTotalBayar());
            summary.setSplitBillCreatorType(trx.getSplitBillCreatorType());

            return summary;

        }).collect(Collectors.toList());

    }


    // GROUP BY kategori_pengeluaran per user
    public List<SettlementSumaryTotalKategoriDto> getSettlementSummaryHeader(Integer dibuatOleh) {
        List<Transactions> transactions = repo.findByDibuatOlehAndDibayarOleh(dibuatOleh, dibuatOleh);

        Map<String, BigDecimal> totalByKategori = transactions.stream()
                .collect(Collectors.groupingBy(
                        trx -> trx.getKategoriPengeluaran() == null ? "UNKNOWN" : trx.getKategoriPengeluaran(),
                        Collectors.reducing(BigDecimal.ZERO, Transactions::getAmount, BigDecimal::add)
                ));

        return totalByKategori.entrySet().stream().map(entry -> {
            SettlementSumaryTotalKategoriDto summary = new SettlementSumaryTotalKategoriDto();
            summary.setKategoriPengeluaran(entry.getKey());
            summary.setTotalAmount(entry.getValue());
            return summary;
        }).collect(Collectors.toList());
    }
}
