package com.acme.pfm.categorize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public final class RulesLoader {
    private RulesLoader() {}

    public static Ruleset loadFromClasspath(String resource) {
        try (InputStream in = RulesLoader.class.getResourceAsStream(resource)) {
            if (in == null) throw new IllegalArgumentException("Rules not found: " + resource);
            return postProcess(new ObjectMapper(new YAMLFactory()).readValue(in, Ruleset.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load rules: " + e.getMessage(), e);
        }
    }

    public static Ruleset loadFromFile(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            return postProcess(new ObjectMapper(new YAMLFactory()).readValue(in, Ruleset.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load rules: " + e.getMessage(), e);
        }
    }

    private static Ruleset postProcess(Ruleset rs) {
        if (rs == null || rs.rules == null) throw new IllegalArgumentException("Empty ruleset");
        for (Rule r : rs.rules) {
            if (r.description_regex != null && !r.description_regex.isBlank()) {
                r.descPattern = Pattern.compile(r.description_regex);
            }
        }
        return rs;
    }
}
