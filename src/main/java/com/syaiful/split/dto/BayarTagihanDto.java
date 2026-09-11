package com.syaiful.split.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BayarTagihanDto {

    private int id;
    private String namaTransaksi;

    private String statusPembayaran;

    private String kategoriPengeluaran;

    private Integer dibuatOleh;

    private LocalDateTime waktuDibuat;

    private Integer dibayarOleh;

    private LocalDateTime waktuPembayaran;

    private BigDecimal amount;

    private String splitBillCode;

    private boolean status;

    private String message;

}
