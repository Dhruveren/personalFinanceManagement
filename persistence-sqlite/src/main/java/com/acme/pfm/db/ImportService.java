package com.acme.pfm.db;

import com.acme.pfm.CsvTransactionParser;
import com.acme.pfm.TransactionMapper;
import com.acme.pfm.categorize.Categorizer;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;

public class ImportService {
    private final String jdbcUrl;
    private final CsvTransactionParser parser;
    private final DateTimeFormatter fmt;
    private final Categorizer categorizer;

    public ImportService(String jdbcUrl, DateTimeFormatter fmt) {
        this(jdbcUrl, fmt, null);
    }

    public ImportService(String jdbcUrl, DateTimeFormatter fmt, Categorizer categorizer) {
        this.jdbcUrl = jdbcUrl;
        this.parser = new CsvTransactionParser();
        this.fmt = fmt;
        this.categorizer = categorizer;
    }

    public int importCsv(Path csvPath) throws Exception {
        new SchemaBootstrap(jdbcUrl, "schema.sql").run();

        var rows = parser.parse(csvPath);
        var txns = rows.stream().map(r -> TransactionMapper.from(r, fmt)).toList();

        if (txns.isEmpty()) return 0;

        if (categorizer != null) {
            txns = txns.stream().map(t -> {
                String cat = t.getCategory();
                if (cat == null || cat.isBlank() || "Uncategorized".equalsIgnoreCase(cat)) {
                    String newCat = categorizer.categorize(t);
                    return new com.acme.pfm.Transaction(t.getId(), t.getDate(), t.getDescription(), t.getAmount(), newCat);
                }
                return t;
            }).toList();
        }

        String sql = "INSERT INTO transactions(id, date, amount, description, category) VALUES (?, ?, ?, ?, ?)";
        int inserted = 0;

        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {

            c.setAutoCommit(false);
            for (var t : txns) {
                ps.setString(1, t.getId());
                ps.setString(2, t.getDate().toString());
                ps.setBigDecimal(3, t.getAmount());
                ps.setString(4, t.getDescription());
                ps.setString(5, t.getCategory());
                ps.addBatch();
            }
            int[] counts = ps.executeBatch();
            c.commit();

            for (int n : counts) {
                inserted += (n >= 0) ? n : 1; // SUCCESS_NO_INFO
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV import failed: " + e.getMessage(), e);
        }

        return inserted;
    }
}
