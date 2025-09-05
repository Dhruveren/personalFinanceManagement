package com.acme.pfm.cli.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public final class Config {
    private final Properties props = new Properties();

    private Config() { }

    public static Config load(Path overrideFile) {
        Config c = new Config();
        try (InputStream in = Config.class.getResourceAsStream("/pfm.properties")) {
            if (in != null) c.props.load(in);
        } catch (Exception ignored) { }
        if (overrideFile != null && Files.isRegularFile(overrideFile)) {
            try (InputStream in = Files.newInputStream(overrideFile)) {
                Properties p = new Properties();
                p.load(in);
                p.forEach((k, v) -> c.props.put(k, v));
            } catch (Exception ignored) { }
        }
        // Environment / system overrides
        System.getProperties().forEach((k, v) -> c.props.put(k, v));
        System.getenv().forEach((k, v) -> c.props.put(k, v));
        return c;
    }

    public String dbUrl() {
        String raw = props.getProperty("db.url");
        if (raw == null) return "jdbc:sqlite:" + System.getProperty("user.home") + "/Developer/SelfFinanceTracker/pfm/pfm.db";
        return raw.replace("${user.home}", System.getProperty("user.home"));
    }

    public DateTimeFormatter csvFormatter() {
        String fmt = props.getProperty("csv.date.format", "uuuu-MM-dd");
        return DateTimeFormatter.ofPattern(fmt);
    }
}
