package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.acme.pfm.cli.reports.ReportGenerator;
import com.acme.pfm.cli.config.Config;
import com.acme.pfm.cli.services.ServiceFactory;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "report-export",
        description = "Export monthly HTML report with charts",
        mixinStandardHelpOptions = true)
public class ReportExportCommand implements Callable<Integer> {

    private final ReportGenerator generator;

    /** No-arg constructor for picocli */
    public ReportExportCommand() {
        // Load default or environment config
        Config cfg = Config.load(null);
        ServiceFactory sf = ServiceFactory.getInstance(cfg);
        this.generator = sf.getReportGenerator();
    }

    /** Constructor used by custom factory */
    public ReportExportCommand(ReportGenerator generator) {
        this.generator = generator;
    }

    @Parameters(index = "0", description = "Month in YYYY-MM")
    String month;

    @Option(names = {"-o", "--out"}, description = "Output HTML file path", required = true)
    Path out;

    @Override
    public Integer call() {
        try {
            if (!month.matches("\\d{4}-\\d{2}")) {
                System.err.println("Invalid month format. Use YYYY-MM.");
                return 1;
            }
            Path outHtml = out.toAbsolutePath();
            generator.generateMonthly(month, outHtml);
            System.out.println("✔ Report exported to: " + outHtml);
            return 0;
        } catch (Exception e) {
            System.err.println("❌ Export failed: " + e.getMessage());
            return 1;
        }
    }
}
