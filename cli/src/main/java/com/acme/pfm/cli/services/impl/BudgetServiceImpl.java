package com.acme.pfm.cli.services.impl;

import com.acme.pfm.cli.services.interfaces.BudgetService;
import com.acme.pfm.core.BudgetRepositoryPort;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

public class BudgetServiceImpl implements BudgetService {
    private static final Pattern YYYY_MM = Pattern.compile("^\\d{4}-\\d{2}$");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final BudgetRepositoryPort repo;

    public BudgetServiceImpl(BudgetRepositoryPort repo) {
        this.repo = repo;
    }

    private static void requireMonth(String month) {
        if (month == null || !YYYY_MM.matcher(month).matches()) {
            throw new IllegalArgumentException("Month must be YYYY-MM");
        }
    }

    private static String requireCategory(String category) {
        String c = category == null ? "" : category.trim();
        if (c.isEmpty()) throw new IllegalArgumentException("Category is required");
        return c;
    }

    private static BigDecimal requireNonNegative(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }
        // Keep scale as provided; repository/DB can preserve NUMERIC precision
        return amount.stripTrailingZeros();
    }

    @Override
    public int setBudget(String month, String category, BigDecimal amount) {
        requireMonth(month);
        String cat = requireCategory(category);
        BigDecimal amt = requireNonNegative(amount);
        return repo.upsert(month, cat, amt);
    }

    @Override
    public BigDecimal getBudget(String month, String category) {
        requireMonth(month);
        String cat = requireCategory(category);
        return repo.get(month, cat);
    }

    @Override
    public int deleteBudget(String month, String category) {
        requireMonth(month);
        String cat = requireCategory(category);
        return repo.delete(month, cat);
    }

    @Override
    public List<BudgetLine> listBudgets(String month) {
        requireMonth(month);
        var rows = repo.list(month);
        List<BudgetLine> out = new ArrayList<>(rows.size());
        for (var r : rows) {
            // assuming port row has public fields `category` and `amount`
            out.add(new BudgetLine(r.category, r.amount));
        }
        // sort for stable output
        out.sort(Comparator.comparing(b -> b.category));
        return out;
    }

    @Override
    public List<BudgetReportRow> monthlyReport(String month, boolean includeZero) {
        requireMonth(month);

        // budgets map: category -> amount
        Map<String, BigDecimal> budgets = new LinkedHashMap<>();
        for (var r : repo.list(month)) {
            budgets.put(r.category, r.amount != null ? r.amount : ZERO);
        }

        // spend map from repository (assumed month/category aggregation over transactions)
        Map<String, BigDecimal> spend = repo.spendByCategory(month);
        if (spend == null) spend = Map.of();

        // union of categories
        Set<String> cats = new LinkedHashSet<>();
        cats.addAll(budgets.keySet());
        cats.addAll(spend.keySet());

        List<BudgetReportRow> out = new ArrayList<>();
        for (String cat : cats) {
            BigDecimal b = budgets.getOrDefault(cat, ZERO);
            BigDecimal s = spend.getOrDefault(cat, ZERO);
            if (!includeZero && b.signum() == 0 && s.signum() == 0) continue;
            // BudgetReportRow is assumed to expose category, budget, spend, and derived variance
            out.add(new BudgetReportRow(cat, b, s));
        }

        out.sort(Comparator.comparing(r -> r.category));
        return out;
    }
}
