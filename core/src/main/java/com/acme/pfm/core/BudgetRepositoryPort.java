package com.acme.pfm.core;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BudgetRepositoryPort {
    int upsert(String month, String category, BigDecimal amount);
    BigDecimal get(String month, String category);
    int delete(String month, String category);
    List<BudgetRow> list(String month); // list budgets for a month

    // Aggregation from transactions for a month
    Map<String, BigDecimal> spendByCategory(String month); // sums expenses (negative amounts -> absolute)

    final class BudgetRow {
        public final String month;
        public final String category;
        public final BigDecimal amount;
        public BudgetRow(String month, String category, BigDecimal amount) {
            this.month = month; this.category = category; this.amount = amount;
        }
    }
}
