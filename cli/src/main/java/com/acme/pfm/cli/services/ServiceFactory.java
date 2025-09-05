package com.acme.pfm.cli.services;

import com.acme.pfm.cli.config.Config;
import com.acme.pfm.core.TransactionRepositoryPort;
import com.acme.pfm.db.SQLiteTransactionRepository;
import com.acme.pfm.db.ImportService;
import com.acme.pfm.cli.services.interfaces.TransactionService;
import com.acme.pfm.cli.services.impl.TransactionServiceImpl;

public class ServiceFactory {
    private static ServiceFactory instance;

    private final TransactionService transactionService;
    private final ImportService importService;

    private ServiceFactory(Config cfg) {
        String url = cfg.dbUrl();
        var repo = new SQLiteTransactionRepository(url);           // concrete
        TransactionRepositoryPort port = repo;                     // as port
        this.transactionService = new TransactionServiceImpl(port);
        this.importService = new ImportService(url, cfg.csvFormatter());
    }

    public static synchronized ServiceFactory getInstance(Config cfg) {
        if (instance == null) instance = new ServiceFactory(cfg);
        return instance;
    }

    public TransactionService getTransactionService() { return transactionService; }
    public ImportService getImportService() { return importService; }
}
