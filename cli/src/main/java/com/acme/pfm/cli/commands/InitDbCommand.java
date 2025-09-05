package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.acme.pfm.db.ImportService;
import picocli.CommandLine.Parameters;
import com.acme.pfm.cli.config.Config;
import com.acme.pfm.cli.services.ServiceFactory;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "init-db",
        description = "Initialize or update the database schema",
        mixinStandardHelpOptions = true)
public class InitDbCommand implements Callable<Integer> {

    @Option(names="--config", description="Path to properties file")
    private Path configPath;

    @Override
    public Integer call() throws Exception {
        // Load config (same as main)
        var cfg = Config.load(configPath);
        // Build ServiceFactory and get ImportService
        var sf = ServiceFactory.getInstance(cfg);
        ImportService svc = sf.getImportService();
        // Run schema bootstrap by importing an empty CSV
        svc.importCsv(Path.of("/dev/null"));
        System.out.println("✔ Database schema initialized.");
        return 0;
    }
}
