package com.acme.pfm.db;

import com.acme.pfm.CsvTransactionParser;
import com.acme.pfm.TransactionMapper;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        // Ensure schema exists
        new SchemaBootstrap(jdbcUrl, "schema.sql").run();
        // Parse and map
        var rows = parser.parse(csvPath);
        var txns = rows.stream().map(r -> TransactionMapper.from(r, fmt)).toList();
        // Batch save
        var repo = new SQLiteTransactionRepository(jdbcUrl);
        int[] counts = repo.saveAll(txns);
        return counts.length;
    }
}