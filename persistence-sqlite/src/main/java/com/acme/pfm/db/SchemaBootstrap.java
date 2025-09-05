package com.acme.pfm.db;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaBootstrap {
    private final String jdbcUrl;
    private final String resourceName;

    public SchemaBootstrap(String jdbcUrl, String resourceName) {
        this.jdbcUrl = jdbcUrl;
        this.resourceName = resourceName;
    }

    public void run() throws IOException, SQLException {
        // Load schema.sql from the classpath
        var cl = Thread.currentThread().getContextClassLoader();
        try (var in = cl.getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new SQLException("Resource not found: " + resourceName);
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            // Execute via jdbc
            try (Connection con = DriverManager.getConnection(jdbcUrl);
                 Statement st = con.createStatement()) {
                // splti by semicolon if multiple statements
                for (String stmt : sql.split(";")) {
                    String s = stmt.trim();
                    if (!s.isEmpty()) {
                        st.executeUpdate(s);
                    }
                }
            }
        }
    }

    // convinience main to run from ide
    public static void main(String[] args) throws IOException, SQLException {
        String url = "jdbc:sqlite:pfm:db";
        new SchemaBootstrap(url, "schema.sql").run();
        System.out.println("Schema bootstrap complete.");
    }

}
