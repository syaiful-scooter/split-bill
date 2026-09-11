package com.syaiful.split.controller;

import com.syaiful.split.dto.BayarDto;
import com.syaiful.split.dto.BayarTagihanDto;
import com.syaiful.split.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TransactionBayarController {
    @Autowired
    private TransactionService trxSrv;

    @PutMapping("/bayar")
    public ResponseEntity<Map<String, Object>> bayarBagiRata(@RequestBody BayarDto b){
        List<BayarTagihanDto> result = trxSrv.bayarTagihanku(b);
        BayarTagihanDto data = result.get(0);

        if (!data.isStatus()){
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", data.isStatus(),
                            "message", data.getMessage()
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "status", data.isStatus(),
                        "message", data.getMessage()
                )
        );
    }
}
