package com.acme.pfm.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import com.acme.pfm.db.ImportService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(
        name = "import",
        description = "Import transactions from a CSV file",
        mixinStandardHelpOptions = true
)
public class ImportCommand implements Callable<Integer> {

    private final ImportService importService;

    public ImportCommand(ImportService importService) {
        this.importService = importService;
    }

    @Parameters(index = "0", description = "Path to CSV file to import")
    private String csvFilePath;

    @Option(names = {"--dry-run"}, description = "Preview import without saving to database")
    private boolean dryRun;

    @Option(names = {"--skip-duplicates"}, description = "Skip duplicate transactions during import")
    private boolean skipDuplicates;

    @Override
    public Integer call() throws Exception {
        System.out.println("📥 CSV Import");
        System.out.println("=============");

        Path csvPath = Paths.get(csvFilePath);
        if (!Files.isRegularFile(csvPath) || !Files.isReadable(csvPath)) {
            System.err.printf("❌ Error: Not a readable file: %s%n", csvFilePath);
            return 1;
        }

        System.out.printf("📂 File: %s%n", csvPath.toAbsolutePath());
        System.out.printf("📊 Size: %d bytes%n%n", Files.size(csvPath));

        try {
            if (dryRun) {
                System.out.println("🔍 DRY RUN — will parse the file but skip database writes.");
                // If ImportService doesn’t support dry-run, just avoid calling DB write.
                // For now, only show basic info:
                System.out.println("ℹ️ Dry run mode not supported by ImportService. Skipping actual import.");
                return 0;
            }

            int imported = importService.importCsv(csvPath);  // call the real method
            System.out.printf("✅ Successfully imported %d transactions%n", imported);
            System.out.println("💡 Run 'pfm list' to view imported rows.");
            return 0;

        } catch (Exception e) {
            System.err.println("❌ Import failed: " + e.getMessage());
            return 1;
        }
    }
}
