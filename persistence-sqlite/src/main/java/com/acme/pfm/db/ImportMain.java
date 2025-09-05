package com.acme.pfm.db;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class ImportMain {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: ImportMain <absolute-path-to-Transaction.csv>");
            return;
        }
        String url = "jdbc:sqlite:/Users/debasishsarkar/Developer/SelfFinanceTracker/pfm/pfm.db";
        new SchemaBootstrap(url, "schema.sql").run();
        var svc = new ImportService(url, DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        int n = svc.importCsv(Path.of(args[0])); // use args, not args
        System.out.println("Imported " + n + " rows");
    }
}