package com.syaiful.split.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalPerKategoriDto {
    private String kategoriPengeluaran;
    private BigDecimal total;
}
