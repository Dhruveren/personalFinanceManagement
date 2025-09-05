package com.acme.pfm.cli.services.impl;

import com.acme.pfm.cli.services.interfaces.TransactionService;
import com.acme.pfm.cli.services.dto.TransactionDto;
import com.acme.pfm.core.TransactionRepositoryPort;

import java.util.*;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepositoryPort repo;

    public TransactionServiceImpl(TransactionRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public List<TransactionDto> findTransactions(String month, String category, Double min, Double max, String type, int limit) {
        var rows = repo.find(month, category, min, max, type, limit);
        List<TransactionDto> out = new ArrayList<>(rows.size());
        for (var r : rows) {
            out.add(new TransactionDto(r.date, r.amount, r.description, r.category, r.type));
        }
        return out;
    }

    @Override
    public boolean addTransaction(double amount, String description, String category, String type, String date) {
        return repo.insert(date, amount, description, category, type) == 1;
    }

    @Override
    public Map<String, Object> getSummary(String period, String month) {
        Map<String, Object> m = new HashMap<>();
        m.put("income", repo.sumByType(period, month, "income"));
        m.put("expense", repo.sumByType(period, month, "expense"));
        m.put("count", repo.count(period, month));
        m.put("byCategory", repo.sumByCategory(period, month));
        return m;
    }

    @Override
    public List<String> getCategories() { return repo.categories(); }

    @Override
    public int getTotalCount() { return repo.count(null, null); }
}
