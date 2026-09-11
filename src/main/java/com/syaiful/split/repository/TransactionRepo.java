package com.syaiful.split.repository;

import com.syaiful.split.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByDibuatOleh(Integer dibuatOleh);
    List<Transactions> findByDibuatOlehAndKategoriPengeluaranIgnoreCase(Integer dibuatOleh, String kategoriPengeluaran);
    List<Transactions> findByDibuatOlehAndDibayarOleh(Integer dibuatOleh, Integer dibayarOleh);
    List<Transactions> findBySplitBillCode(String splitBillCode);
    List<Transactions> findBySplitBillCodeAndDibayarOleh(String splitBillCode, Integer dibayarOleh);
}
