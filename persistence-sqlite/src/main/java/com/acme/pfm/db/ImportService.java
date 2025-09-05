package com.acme.pfm.db;

import com.acme.pfm.CsvTransactionParser;
import com.acme.pfm.TransactionMapper;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;

public class ImportService {
    private final String jdbcUrl;
    private final CsvTransactionParser parser;
    private final DateTimeFormatter fmt;

    public ImportService(String jdbcUrl, DateTimeFormatter fmt) {
        this.jdbcUrl = jdbcUrl;
        this.parser = new CsvTransactionParser();
        this.fmt = fmt;
    }

    public int importCsv(Path csvPath) throws Exception {
        // 1) Ensure schema exists
        new SchemaBootstrap(jdbcUrl, "schema.sql").run();

        // 2) Parse and map CSV → domain
        var rows = parser.parse(csvPath);
        var txns = rows.stream().map(r -> TransactionMapper.from(r, fmt)).toList();

        if (txns.isEmpty()) return 0;

        // 3) Batch insert in one transaction (fast and atomic)
        String sql = "INSERT INTO transactions(date, amount, description, category, type) VALUES (?, ?, ?, ?, ?)";
        int inserted = 0;

        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {

            c.setAutoCommit(false);
            for (var t : txns) {
                ps.setString(1, t.getId());                       // id
                ps.setString(2, t.getDate().toString());          // ISO yyyy-MM-dd
                ps.setBigDecimal(3, t.getAmount());               // BigDecimal preferred
                ps.setString(4, t.getDescription());
                ps.setString(5, t.getCategory());
                ps.addBatch();
            }
            int[] counts = ps.executeBatch();
            c.commit();

            // Count total rows affected
            for (int n : counts) {
                if (n >= 0) inserted += n;   // SUCCESS_NO_INFO may be -2; treat as 1 if preferred
                else if (n == java.sql.Statement.SUCCESS_NO_INFO) inserted += 1;
            }
        } catch (Exception e) {
            // Re-throw with context
            throw new RuntimeException("CSV import failed: " + e.getMessage(), e);
        }

        return inserted;
    }
}
