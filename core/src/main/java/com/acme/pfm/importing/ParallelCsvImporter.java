package com.acme.pfm.importing;

import com.acme.pfm.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ParallelCsvImporter {
    private static final Logger log = LoggerFactory.getLogger(ParallelCsvImporter.class);
    private final ExecutorService pool;

    public ParallelCsvImporter(ExecutorService pool) {
        this.pool = pool;
    }

    public List<Transaction> parse(Path csv, int chunkSize) throws Exception {
        List<List<String>> chunks = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(csv)) {
            String header = br.readLine(); // skip header
            List<String> buf = new ArrayList<>(chunkSize);
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isBlank()) {
                    buf.add(line);
                    if (buf.size() == chunkSize) {
                        chunks.add(buf);
                        buf = new ArrayList<>(chunkSize);
                    }
                }
            }
            if (!buf.isEmpty()) chunks.add(buf);
        }
        log.debug("CSV chunks: {}", chunks.size());

        List<CompletableFuture<List<Transaction>>> futures = new ArrayList<>(chunks.size());
        for (List<String> c : chunks) {
            futures.add(CompletableFuture.supplyAsync(() -> parseChunk(c), pool));
        }

        // Fix: Add [0] to create proper array
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Transaction>> allTx = all.thenApply(v ->
                futures.stream().flatMap(f -> f.join().stream()).collect(Collectors.toList())
        );
        return allTx.get();
    }

    private List<Transaction> parseChunk(List<String> lines) {
        List<Transaction> out = new ArrayList<>(lines.size());
        for (String s : lines) {
            String[] a = splitCsv(s);
            // Fix: Use correct array indices for CSV format: id,date,amount,description,category
            String id = a.length > 0 ? trimQuotes(a[0]) : "";
            LocalDate date = a.length > 1 ? LocalDate.parse(trimQuotes(a[1])) : LocalDate.now(); // Fix: a[1] not a[21]
            BigDecimal amount = a.length > 2 ? new BigDecimal(trimQuotes(a[2])) : BigDecimal.ZERO; // Fix: a[2] not a[22]
            String desc = a.length > 3 ? trimQuotes(a[3]) : ""; // Fix: a[3] not a[0]
            String cat = a.length > 4 ? trimQuotes(a[4]) : ""; // Fix: a[4] not a[0]

            Transaction t = new Transaction(id, date, desc, amount, cat);
            out.add(t);
        }
        return out;
    }

    private static String trimQuotes(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            return t.substring(1, t.length() - 1);
        }
        return t;
    }

    private String[] splitCsv(String s) {
        // Split on commas not enclosed in double quotes
        return s.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
