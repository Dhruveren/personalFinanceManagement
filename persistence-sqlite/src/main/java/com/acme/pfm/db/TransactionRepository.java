package com.acme.pfm.db;

import com.acme.pfm.Transaction;

import java.util.List;

public interface TransactionRepository {
    void save(Transaction t) throws Exception;

    Transaction findById(String id) throws Exception;

    List<Transaction> findAll() throws Exception;

    boolean update(Transaction t) throws Exception;

    boolean deleteById(String id) throws Exception;

    int batchInsert(List<Transaction> ready);
}
