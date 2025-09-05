package com.acme.pfm.cli.services.impl;

import com.acme.pfm.cli.services.interfaces.BudgetService;
import com.acme.pfm.core.BudgetRepositoryPort;

import java.math.BigDecimal;
import java.util.*;

public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepositoryPort repo;

    public BudgetServiceImpl(BudgetRepositoryPort repo) { this.repo = repo; }

    @Override
    public int setBudget(String month, String category, BigDecimal amount) {
        return repo.upsert(month, category, amount);
    }

    @Override
    public BigDecimal getBudget(String month, String category) {
        return repo.get(month, category);
    }

    @Override
    public int deleteBudget(String month, String category) {
        return repo.delete(month, category);
    }

    @Override
    public List<BudgetLine> listBudgets(String month) {
        var rows = repo.list(month);
        List<BudgetLine> out = new ArrayList<>(rows.size());
        for (var r : rows) out.add(new BudgetLine(r.category, r.amount));
        return out;
    }

    @Override
    public List<BudgetReportRow> monthlyReport(String month, boolean includeZero) {
        Map<String, BigDecimal> spend = repo.spendByCategory(month);
        Map<String, BigDecimal> budgets = new LinkedHashMap<>();
        for (var r : repo.list(month)) budgets.put(r.category, r.amount);

        // union of categories present in either spend or budget
        Set<String> cats = new LinkedHashSet<>(budgets.keySet());
        cats.addAll(spend.keySet());

        List<BudgetReportRow> out = new ArrayList<>();
        for (String cat : cats) {
            BigDecimal b = budgets.get(cat);
            BigDecimal s = spend.get(cat);
            if (!includeZero && (b == null || b.signum() == 0) && (s == null || s.signum() == 0)) continue;
            out.add(new BudgetReportRow(cat, b, s));
        }
        // optional: sort categories alphabetically or by variance
        out.sort(Comparator.comparing(r -> r.category));
        return out;
    }
}
