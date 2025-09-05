package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.acme.pfm.cli.services.interfaces.TransactionService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Command(
        name = "add",
        description = "Add a new transaction manually",
        mixinStandardHelpOptions = true
)
public class AddCommand implements Callable<Integer> {

    private final TransactionService transactionService;

    public AddCommand(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Option(names = {"-a", "--amount"},
            description = "Transaction amount (required)",
            required = true)
    private Double amount;

    @Option(names = {"-d", "--description"},
            description = "Transaction description (required)",
            required = true)
    private String description;

    @Option(names = {"-c", "--category"},
            description = "Transaction category (e.g., Food, Transport, Income)",
            defaultValue = "Other")
    private String category;

    @Option(names = {"-t", "--type"},
            description = "Transaction type: income or expense",
            defaultValue = "expense")
    private String type;

    @Option(names = {"--date"},
            description = "Transaction date (YYYY-MM-DD format, default: today)")
    private String date;

    @Override
    public Integer call() {
        System.out.println("➕ Adding New Transaction");
        System.out.println("========================");

        // 1. Validate transaction type
        if (!"income".equalsIgnoreCase(type) && !"expense".equalsIgnoreCase(type)) {
            System.err.println("❌ Error: Type must be 'income' or 'expense'");
            return 1;
        }

        // 2. Determine date
        String transactionDate = date != null
                ? date
                : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 3. Validate date format
        try {
            LocalDate.parse(transactionDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.err.println("❌ Error: Invalid date format. Use YYYY-MM-DD");
            return 1;
        }

        // 4. Display details
        System.out.println("📋 Transaction Details:");
        System.out.printf("   Date:        %s%n", transactionDate);
        System.out.printf("   Amount:      ₹%.2f%n", amount);
        System.out.printf("   Description: %s%n", description);
        System.out.printf("   Category:    %s%n", category);
        System.out.printf("   Type:        %s%n", type.toUpperCase());

        // 5. Save to database via service
        try {
            boolean saved = transactionService.addTransaction(
                    amount, description, category, type, transactionDate);

            if (saved) {
                System.out.println();
                System.out.println("✅ Transaction added successfully!");
                System.out.println("💡 Run 'pfm list' to view all transactions");
                return 0;
            } else {
                System.err.println();
                System.err.println("❌ Failed to add transaction (no rows affected).");
                return 1;
            }
        } catch (Exception ex) {
            System.err.println();
            System.err.println("❌ Error saving transaction: " + ex.getMessage());
            return 1;
        }
    }
}
