package com.acme.pfm.cli.services.interfaces;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {
    int setBudget(String month, String category, BigDecimal amount);
    BigDecimal getBudget(String month, String category);
    int deleteBudget(String month, String category);
    List<BudgetLine> listBudgets(String month);
    List<BudgetReportRow> monthlyReport(String month, boolean includeZero);

    class BudgetLine {
        public final String category;
        public final BigDecimal amount;
        public BudgetLine(String category, BigDecimal amount) {
            this.category = category; this.amount = amount;
        }
    }

    class BudgetReportRow {
        public final String category;
        public final BigDecimal budget;
        public final BigDecimal spend;
        public final BigDecimal variance; // budget - spend (positive means under budget)
        public BudgetReportRow(String category, BigDecimal budget, BigDecimal spend) {
            this.category = category;
            this.budget = budget == null ? BigDecimal.ZERO : budget;
            this.spend = spend == null ? BigDecimal.ZERO : spend;
            this.variance = this.budget.subtract(this.spend);
        }
    }
}
