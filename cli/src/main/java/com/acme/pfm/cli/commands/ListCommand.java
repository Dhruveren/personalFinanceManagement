package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.services.interfaces.TransactionService;
import com.acme.pfm.cli.services.dto.TransactionDto;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "list",
        description = "List transactions with optional filters",
        mixinStandardHelpOptions = true
)
public class ListCommand implements Callable<Integer> {

    private final TransactionService transactionService;

    // Constructor injection
    public ListCommand(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Option(names = {"-m", "--month"}, description = "Filter by month (YYYY-MM)")
    private String month;

    @Option(names = {"-c", "--category"}, description = "Filter by category")
    private String category;

    @Option(names = {"-l", "--limit"}, description = "Max transactions to show", defaultValue = "10")
    private int limit;

    @Option(names = {"--min-amount"}, description = "Minimum amount")
    private Double minAmount;

    @Option(names = {"--max-amount"}, description = "Maximum amount")
    private Double maxAmount;

    @Option(names = {"-t", "--type"}, description = "Type: income or expense")
    private String type;

    @Override
    public Integer call() throws Exception {
        try {
            System.out.println("📊 Your Transactions");
            System.out.println("===================");

            // Get transactions from database
            List<TransactionDto> transactions = transactionService.findTransactions(
                    month, category, minAmount, maxAmount, type, limit);

            if (transactions.isEmpty()) {
                System.out.println("😔 No transactions found matching your criteria.");
                System.out.println("💡 Try adjusting your filters or add some transactions first.");
                return 0;
            }

            // Show applied filters
            showFilters();

            // Display transactions table
            displayTransactions(transactions);

            return 0;

        } catch (Exception e) {
            System.err.println("❌ Error retrieving transactions: " + e.getMessage());
            return 1;
        }
    }

    private void showFilters() {
        boolean hasFilters = false;
        System.out.println("🔍 Applied Filters:");

        if (month != null) {
            System.out.println("   Month: " + month);
            hasFilters = true;
        }
        if (category != null) {
            System.out.println("   Category: " + category);
            hasFilters = true;
        }
        if (type != null) {
            System.out.println("   Type: " + type);
            hasFilters = true;
        }
        if (minAmount != null || maxAmount != null) {
            System.out.printf("   Amount: ₹%.2f - ₹%.2f%n",
                    minAmount != null ? minAmount : 0.0,
                    maxAmount != null ? maxAmount : Double.MAX_VALUE);
            hasFilters = true;
        }

        if (!hasFilters) {
            System.out.println("   None (showing all transactions)");
        }
        System.out.printf("   Limit: %d transactions%n%n", limit);
    }

    private void displayTransactions(List<TransactionDto> transactions) {
        // Table header
        System.out.printf("%-12s %-12s %-30s %-12s %-8s%n",
                "Date", "Amount", "Description", "Category", "Type");
        System.out.println("─".repeat(85));

        // Transaction rows
        double totalIncome = 0.0, totalExpense = 0.0;
        for (TransactionDto transaction : transactions) {
            System.out.printf("%-12s ₹%-11.2f %-30s %-12s %-8s%n",
                    transaction.getDate(),
                    transaction.getAmount(),
                    truncate(transaction.getDescription(), 28),
                    transaction.getCategory(),
                    transaction.getType().toUpperCase());

            if ("income".equalsIgnoreCase(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }

        System.out.println("─".repeat(85));
        System.out.printf("💰 Summary: %d transactions | Income: ₹%.2f | Expense: ₹%.2f | Net: ₹%.2f%n",
                transactions.size(), totalIncome, totalExpense, (totalIncome - totalExpense));
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
