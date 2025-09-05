package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.services.interfaces.TransactionService; // add this
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Command(
        name = "summary",
        description = "Show financial summary and statistics",
        mixinStandardHelpOptions = true
)
public class SummaryCommand implements Callable<Integer> {

    // Injected dependency from ServiceCommandFactory
    private final TransactionService transactionService;

    // Constructor that ServiceCommandFactory calls
    public SummaryCommand(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Option(names = {"-p", "--period"},
            description = "Summary period: monthly, weekly, yearly",
            defaultValue = "monthly")
    private String period;

    @Option(names = {"-m", "--month"},
            description = "Specific month for summary (YYYY-MM format)")
    private String month;

    @Option(names = {"--detailed"},
            description = "Show detailed breakdown by category")
    private boolean detailed;

    @Override
    public Integer call() throws Exception {
        System.out.println("📊 Financial Summary");
        System.out.println("===================");

        String summaryPeriod = month != null
                ? month
                : (period.equals("monthly")
                ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                : period);

        System.out.printf("📅 Period: %s (%s)%n%n", summaryPeriod, period);

        // TODO: later compute real values from transactionService
        System.out.println("💰 Overview:");
        System.out.printf("   Total Income:  ₹%,10.2f%n", 25000.00);
        System.out.printf("   Total Expense: ₹%,10.2f%n", 18500.00);
        System.out.printf("   Net Savings:   ₹%,10.2f%n", 6500.00);
        System.out.println();

        System.out.printf("📈 Savings Rate: %.1f%%%n", (6500.0 / 25000.0) * 100);
        System.out.printf("📊 Total Transactions: %d%n%n", 45);

        if (detailed) {
            System.out.println("📂 Category Breakdown:");
            System.out.println("─".repeat(40));
            System.out.printf("%-15s %-12s %-8s%n", "Category", "Amount", "Count");
            System.out.println("─".repeat(40));
            System.out.printf("%-15s ₹%-11.2f %d%n", "Food", 5500.00, 12);
            System.out.printf("%-15s ₹%-11.2f %d%n", "Transport", 3200.00, 8);
            System.out.printf("%-15s ₹%-11.2f %d%n", "Entertainment", 2100.00, 5);
            System.out.printf("%-15s ₹%-11.2f %d%n", "Shopping", 4200.00, 7);
            System.out.printf("%-15s ₹%-11.2f %d%n", "Bills", 3500.00, 4);
            System.out.printf("%-15s ₹%-11.2f %d%n", "Income", 25000.00, 2);
            System.out.println("─".repeat(40));
        }

        System.out.println("💡 Tips:");
        System.out.println("   • Use --detailed flag for category breakdown");
        System.out.println("   • Use --month YYYY-MM for specific month");
        System.out.println("   • Try 'pfm list --category Food' to see food expenses");

        return 0;
    }
}
