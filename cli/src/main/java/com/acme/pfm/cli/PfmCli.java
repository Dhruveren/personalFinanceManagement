package com.acme.pfm.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.commands.*;
import com.acme.pfm.cli.config.Config;
import com.acme.pfm.cli.factory.ServiceCommandFactory;

import java.nio.file.Path;

@Command(
        name = "pfm",
        description = "Personal Finance Manager - Track your expenses and income",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        subcommands = {
                ListCommand.class,
                AddCommand.class,
                SummaryCommand.class,
                ImportCommand.class
        }
)
public class PfmCli implements Runnable {

    // Global option to specify config file
    @Option(names = {"--config"}, description = "Path to properties file")
    private Path configPath;

    public static void main(String[] args) {
        int exit;
        try {
            // First parse only the root options (like --config) on an instance
            PfmCli root = new PfmCli();
            CommandLine bootstrap = new CommandLine(root);
            bootstrap.parseArgs(args); // populates root.configPath

            // Load configuration and build factory
            Config cfg = Config.load(root.configPath);
            ServiceCommandFactory factory = new ServiceCommandFactory(cfg);

            // Now execute the real CLI with dependency-injected commands
            CommandLine cmd = new CommandLine(PfmCli.class, factory);
            exit = cmd.execute(args);
        } catch (Exception e) {
            System.err.println("❌ Startup error: " + e.getMessage());
            exit = 1;
        }
        System.exit(exit);
    }

    @Override
    public void run() {
        System.out.println("💰 Personal Finance Manager CLI");
        System.out.println("===============================");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  📊 list     - List transactions with filters");
        System.out.println("  ➕ add      - Add new transaction manually");
        System.out.println("  📈 summary  - Show financial summary & stats");
        System.out.println("  📥 import   - Import transactions from CSV");
        System.out.println();
        System.out.println("Use 'pfm <command> --help' for detailed options");
        System.out.println("Example: pfm list --month 2025-09 --category Food");
    }
}
