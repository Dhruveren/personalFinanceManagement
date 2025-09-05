package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.services.interfaces.BudgetService;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

@Command(name="budget-set", description="Set or update a category budget for a month", mixinStandardHelpOptions = true)
public class BudgetSetCommand implements Callable<Integer> {
    private final BudgetService service;
    public BudgetSetCommand(BudgetService s) { this.service = s; }

    @Parameters(index="0", description="Month in YYYY-MM") String month;
    @Parameters(index="1", description="Category") String category;
    @Parameters(index="2", description="Amount") BigDecimal amount;

    @Override public Integer call() {
        int n = service.setBudget(month, category, amount);
        System.out.printf("✔ Budget set: %s / %s = %s (%d)%n", month, category, amount, n);
        return 0;
    }
}
