package com.acme.pfm.cli.services.impl;

import com.acme.pfm.cli.services.interfaces.MonthSpendProvider;

import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;



public class MonthSpendProviderSqlite implements com.acme.pfm.cli.services.interfaces.MonthSpendProvider {
    private final DataSource ds;

    public MonthSpendProviderSqlite(DataSource ds) { this.ds = ds; }

    @Override
    public Map<String, BigDecimal> spendByCategory(String month) {
        // Assumption: expenses are stored as positive amounts; if not, adapt CASE expression
        String sql = """
            SELECT category, SUM(CASE WHEN amount > 0 THEN amount ELSE 0 END) AS spend
            FROM transactions
            WHERE substr(date, 1, 7) = ?
            GROUP BY category
        """;
        try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            try (var rs = ps.executeQuery()) {
                Map<String, BigDecimal> out = new HashMap<>();
                while (rs.next()) {
                    out.put(rs.getString(1), rs.getBigDecimal(2));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("compute monthly spend failed", e);
        }
    }
}
