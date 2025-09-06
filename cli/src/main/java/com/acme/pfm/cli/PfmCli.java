package com.acme.pfm.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.commands.*;
import com.acme.pfm.cli.config.Config;
import com.acme.pfm.cli.factory.ServiceCommandFactory;
import com.acme.pfm.cli.commands.ReportExportCommand;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.config.Config;
import com.acme.pfm.cli.factory.ServiceCommandFactory;
import java.nio.file.Path;


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
                ImportCommand.class,

                // Budget commands
                com.acme.pfm.cli.commands.BudgetSetCommand.class,
                com.acme.pfm.cli.commands.BudgetGetCommand.class,
                com.acme.pfm.cli.commands.BudgetListCommand.class,
                com.acme.pfm.cli.commands.BudgetDeleteCommand.class,
                com.acme.pfm.cli.commands.BudgetReportCommand.class,

                ReportExportCommand.class,
                InitDbCommand.class




        }
)
public class PfmCli implements Runnable {

    @Option(names = "--verbose", description = "Enable debug logs")
    private boolean verbose;

    @Option(names = {"--config"}, description = "Path to properties file")
    private Path configPath;

    public static void main(String[] args) {
        int exitCode;
        try {
            PfmCli root = new PfmCli();

            // Build one CommandLine with your DI factory
            Config cfg = Config.load(root.configPath);
            IFactory factory = new ServiceCommandFactory(cfg);
            CommandLine cmd = new CommandLine(root, factory);

            // Parse only root options (--verbose, --config), stop before subcommand
            ParseResult result = cmd.parseArgs(args);
            if (result.hasMatchedOption("--verbose")) {
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
                System.setProperty("org.slf4j.simpleLogger.log.com.acme.pfm", "debug");
            }

            // Now execute the full command (subcommand instantiation via your factory)
            exitCode = cmd.execute(args);

        } catch (Exception ex) {
            System.err.println("❌ Startup error: " + ex.getMessage());
            ex.printStackTrace();
            exitCode = 1;
        }
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // default help text when no subcommand provided
        CommandLine.usage(this, System.out);
    }
}