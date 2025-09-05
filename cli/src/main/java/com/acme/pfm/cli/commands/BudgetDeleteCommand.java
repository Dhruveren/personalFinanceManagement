package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.acme.pfm.cli.services.interfaces.BudgetService;

import java.util.concurrent.Callable;

@Command(name="budget-del", description="Delete a budget for month/category", mixinStandardHelpOptions = true)
public class BudgetDeleteCommand implements Callable<Integer> {
    private final BudgetService service;
    public BudgetDeleteCommand(BudgetService s) { this.service = s; }

    @Parameters(index="0") String month;
    @Parameters(index="1") String category;

    @Override public Integer call() {
        int n = service.deleteBudget(month, category);
        System.out.printf("Deleted %d record(s)%n", n);
        return n > 0 ? 0 : 1;
    }
}
