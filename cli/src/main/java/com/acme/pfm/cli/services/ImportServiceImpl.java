package com.acme.pfm.cli.services;

import com.acme.pfm.Transaction;
import com.acme.pfm.categorize.Categorizer;
import com.acme.pfm.cli.services.interfaces.ImportService;
import com.acme.pfm.db.TransactionRepository;
import com.acme.pfm.importing.ParallelCsvImporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ImportServiceImpl implements ImportService {
    private final TransactionRepository repo;
    private final Categorizer categorizer;
    private final ExecutorService ioPool;
    private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    public ImportServiceImpl(TransactionRepository repo, Categorizer categorizer, ExecutorService pool) {
        this.repo = repo;
        this.categorizer = categorizer;
        this.ioPool = pool;  // injected or create here
    }

    @Override
    public int importCsv(Path csv) throws Exception {
        ParallelCsvImporter importer = new ParallelCsvImporter(ioPool);
        List<Transaction> txs = importer.parse(csv, 200);
        List<CompletableFuture<Transaction>> futures = txs.stream()
                .map(t -> CompletableFuture.supplyAsync(() -> categorize(t), ioPool))
                .toList();

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        List<Transaction> ready = all.thenApply(v -> futures.stream().map(CompletableFuture::join).toList()).get();

        int inserted = repo.batchInsert(ready);
        log.info("Imported {} transactions from {}", inserted, csv);
        return inserted;
    }

    private Transaction categorize(Transaction t) {
        String cat = categorizer.categorize(t);
        t.setCategory(cat);
        return t;
    }
}
