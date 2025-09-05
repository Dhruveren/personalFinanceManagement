package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.services.interfaces.BudgetService;

import java.util.concurrent.Callable;
import java.math.BigDecimal;

@Command(name="budget-report", description="Show spend vs budget for a month", mixinStandardHelpOptions = true)
public class BudgetReportCommand implements Callable<Integer> {
    private final BudgetService service;
    public BudgetReportCommand(BudgetService s) { this.service = s; }

    @Parameters(index="0") String month; // YYYY-MM
    @Option(names="--all", description="Include zero rows") boolean includeZero;

    @Override public Integer call() {
        var rows = service.monthlyReport(month, includeZero);
        if (rows.isEmpty()) {
            System.out.println("No data for month.");
            return 0;
        }
        System.out.println("Category            Budget       Spend     Variance");
        System.out.println("---------------------------------------------------");
        BigDecimal totalB = BigDecimal.ZERO, totalS = BigDecimal.ZERO;
        for (var r : rows) {
            System.out.printf("%-18s %10s %10s %11s%n",
                    r.category, r.budget, r.spend, r.variance);
            totalB = totalB.add(r.budget);
            totalS = totalS.add(r.spend);
        }
        System.out.println("---------------------------------------------------");
        System.out.printf("%-18s %10s %10s %11s%n",
                "TOTAL", totalB, totalS, totalB.subtract(totalS));
        return 0;
    }
}
