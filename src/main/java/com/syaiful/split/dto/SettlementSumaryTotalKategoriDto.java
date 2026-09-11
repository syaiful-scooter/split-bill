package com.syaiful.split.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementSumaryTotalKategoriDto {
    private String kategoriPengeluaran;

    private BigDecimal totalAmount;

}
