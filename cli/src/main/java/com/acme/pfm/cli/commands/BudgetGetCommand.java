package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.acme.pfm.cli.services.interfaces.BudgetService;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

@Command(name="budget-get", description="Get budget amount for month/category", mixinStandardHelpOptions = true)
public class BudgetGetCommand implements Callable<Integer> {
    private BudgetService service;
    public BudgetGetCommand(BudgetService s) { this.service = s; }

    @Parameters(index="0") String month;
    @Parameters(index="1") String category;

    @Override public Integer call() {
        BigDecimal amt = service.getBudget(month, category);
        if (amt == null) {
            System.out.println("No budget found.");
            return 1;
        }
        System.out.printf("%s / %s = %s%n", month, category, amt);
        return 0;
    }
}
