package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.acme.pfm.cli.services.interfaces.BudgetService;

import java.util.concurrent.Callable;

@Command(name="budget-list", description="List all budgets for a month", mixinStandardHelpOptions = true)
public class BudgetListCommand implements Callable<Integer> {
    private final BudgetService service;
    public BudgetListCommand(BudgetService s) { this.service = s; }

    @Parameters(index="0") String month;

    @Override public Integer call() {
        var rows = service.listBudgets(month);
        if (rows.isEmpty()) {
            System.out.println("No budgets for month.");
            return 0;
        }
        System.out.println("Category            Amount");
        System.out.println("----------------------------");
        for (var r : rows) {
            System.out.printf("%-18s %10s%n", r.category, r.amount);
        }
        return 0;
    }
}
