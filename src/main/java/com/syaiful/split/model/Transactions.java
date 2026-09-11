package com.syaiful.split.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaksi")
@Data
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama_transaksi", nullable = false)
    private String namaTransaksi;

    @Column(name = "split_bill_code", nullable = false)
    private String splitBillCode;

    @Column(name = "split_bill_method", nullable = false)
    private String splitBillMethod;

    @Column(name = "is_split_bill", nullable = false)
    private Boolean isSplitBill;

    @Column(name = "split_bill_creator_type")
    private String splitBillCreatorType; // creator/invited

    @Column(name = "status_pembayaran")
    private String statusPembayaran;

    @Column(name = "kategori_pengeluaran")
    private String kategoriPengeluaran;

    @Column(name = "dibuat_oleh")
    private Integer dibuatOleh;

    @Column(name = "waktu_dibuat")
    private LocalDateTime waktuDibuat;

    @Column(name = "dibayar_oleh")
    private Integer dibayarOleh;

    @Column(name = "waktu_pembayaran")
    private LocalDateTime waktuPembayaran;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "service_charge_pct", precision = 5, scale = 4)
    private BigDecimal serviceChargePct;

    @Column(name = "service_charge_amount", precision = 15, scale = 2)
    private BigDecimal serviceChargeAmount;

    @Column(name = "total_bayar", precision = 15, scale = 2)
    private BigDecimal totalBayar;

}