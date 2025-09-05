package com.acme.pfm.cli.services;

import com.acme.pfm.cli.config.Config;
import com.acme.pfm.core.TransactionRepositoryPort;
import com.acme.pfm.db.SQLiteTransactionRepository;
import com.acme.pfm.db.ImportService;
import com.acme.pfm.cli.services.interfaces.TransactionService;
import com.acme.pfm.cli.services.impl.TransactionServiceImpl;
import com.acme.pfm.categorize.Categorizer;
import com.acme.pfm.categorize.RulesLoader;
import com.acme.pfm.categorize.Ruleset;

public class ServiceFactory {
    private static ServiceFactory instance;

    private final TransactionService transactionService;
    private final ImportService importService;

    private ServiceFactory(Config cfg) {
        // 1) Repository adapter (implements the port)
        // inside ServiceFactory constructor
        String url = cfg.dbUrl();
        var repoImpl = new SQLiteTransactionRepository(url);
        TransactionRepositoryPort port = repoImpl;

// load rules from cfg.rulesPath()
        String rulesPath = cfg.rulesPath();
        Ruleset ruleset = rulesPath.startsWith("classpath:")
                ? RulesLoader.loadFromClasspath(rulesPath.substring("classpath:".length()))
                : RulesLoader.loadFromFile(java.nio.file.Paths.get(rulesPath));
        Categorizer categorizer = new Categorizer(ruleset);

        this.transactionService = new TransactionServiceImpl(port);
        this.importService = new ImportService(url, cfg.csvFormatter(), categorizer); // only once

    }

    public static synchronized ServiceFactory getInstance(Config cfg) {
        if (instance == null) instance = new ServiceFactory(cfg);
        return instance;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public ImportService getImportService() {
        return importService;
    }
}
