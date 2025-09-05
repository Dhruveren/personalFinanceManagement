package com.acme.pfm.cli.services.interfaces;

import com.acme.pfm.cli.services.dto.TransactionDto;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<TransactionDto> findTransactions(String month, String category,
                                          Double minAmount, Double maxAmount,
                                          String type, int limit);

    boolean addTransaction(double amount, String description, String category,
                           String type, String date);

    Map<String, Object> getSummary(String period, String month);

    List<String> getCategories();

    int getTotalCount();
}
