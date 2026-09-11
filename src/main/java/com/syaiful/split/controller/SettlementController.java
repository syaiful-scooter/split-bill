package com.syaiful.split.controller;

import com.syaiful.split.dto.SettlementSumaryDto;
import com.syaiful.split.dto.SettlementSumaryTotalKategoriDto;
import com.syaiful.split.model.Transactions;
import com.syaiful.split.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class SettlementController {
    @Autowired
    private TransactionService trxSrv;

    @GetMapping("/settlement/summary/{userId}")
    public ResponseEntity<List<SettlementSumaryDto>> getSettlementSummary(@PathVariable Integer userId) {

        List<SettlementSumaryDto> result = trxSrv.getSettlementSummaryByUserId(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/settlement/totalkategori/{userId}")
    public ResponseEntity<List<SettlementSumaryTotalKategoriDto>> getSettlementSummaryHeader(@PathVariable Integer userId) {

        List<SettlementSumaryTotalKategoriDto> result = trxSrv.getSettlementSummaryHeader(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/settlement/summary/{userId}/{kategoriPengeluaran}")
    public ResponseEntity<List<SettlementSumaryDto>> getSettlementSummaryByCategory(@PathVariable Integer userId,
                                                                                    @PathVariable String kategoriPengeluaran) {

        List<SettlementSumaryDto> result = trxSrv.getSettlementSummaryByUserIdAndKategoriPengeluaran(userId, kategoriPengeluaran);

        return ResponseEntity.ok(result);
    }

}
