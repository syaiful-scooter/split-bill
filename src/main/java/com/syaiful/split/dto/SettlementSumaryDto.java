package com.syaiful.split.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SettlementSumaryDto {
    private Integer transactionId;
    private String splitBillCode;
    private String namaTransaksi;
    private String kategoriPengeluaran;
    private String statusPembayaran;
    private LocalDateTime tglDibuat;
    private LocalDateTime tglBayar;
    private String splitBillCreatorType;
    private BigDecimal amount;
    private BigDecimal servisFee;
    private BigDecimal servisFeePercent;
    private BigDecimal totalBayar;


    private String dibuatOleh;
    private String dibayarOleh;
}
