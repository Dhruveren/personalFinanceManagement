package com.acme.pfm.core;

import java.util.List;
import java.util.Map;

/**
 * Persistence-agnostic port for transaction data access.
 * Persistence adapters (e.g., SQLite) will implement this.
 */
public interface TransactionRepositoryPort {

    // Row projection for CLI service (simple DTO to avoid leaking JDBC types)
    final class TxRow {
        public final String date;         // ISO yyyy-MM-dd
        public final double amount;
        public final String description;
        public final String category;
        public final String type;         // "income" or "expense"

        public TxRow(String date, double amount, String description, String category, String type) {
            this.date = date;
            this.amount = amount;
            this.description = description;
            this.category = category;
            this.type = type;
        }
    }

    // Query with optional filters (null means “don’t filter”)
    List<TxRow> find(String month, String category, Double minAmount, Double maxAmount, String type, int limit);

    // Insert a single transaction; return affected rows (1 on success)
    int insert(String date, double amount, String description, String category, String type);

    // Aggregations for summaries
    double sumByType(String period, String month, String type);

    int count(String period, String month);

    Map<String, Double> sumByCategory(String period, String month);

    // Metadata
    List<String> categories();
}
