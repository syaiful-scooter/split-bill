package com.syaiful.split.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PembayaranBagiRataRequestDto {
    private BigDecimal amount;
    private String namaTransaksi;
    private String isSplitBill;
    private String kategoriPengeluaran;
    private String splitStrategy;
    private List<PembayaranBagiRataDto> datarow;
}
