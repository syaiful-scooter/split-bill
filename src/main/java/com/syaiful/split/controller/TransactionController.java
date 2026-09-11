package com.syaiful.split.controller;

import com.syaiful.split.dto.PembayaranBagiRataRequestDto;
import com.syaiful.split.model.Transactions;
import com.syaiful.split.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/simpan")
public class TransactionController {
    @Autowired
    private TransactionService trxSrv;

    @PostMapping("/")
    public Map<String, Object> simpan(@RequestBody PembayaranBagiRataRequestDto request)
    {

        List<Transactions> result = trxSrv.create(request);

        return Map.of("status", true,
                "message", "Menyimpan dengan pembagian secara merata, Sukses dilakukan",
                "data", result);
    }
}
