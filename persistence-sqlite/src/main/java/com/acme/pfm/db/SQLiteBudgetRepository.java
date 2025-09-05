package com.acme.pfm.db;

import com.acme.pfm.core.BudgetRepositoryPort;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class SQLiteBudgetRepository implements BudgetRepositoryPort {
    private final String url;
    public SQLiteBudgetRepository(String jdbcUrl) { this.url = jdbcUrl; }
    private Connection conn() throws SQLException { return DriverManager.getConnection(url); }

    @Override
    public int upsert(String month, String category, BigDecimal amount) {
        String sql = "INSERT INTO budgets(month, category, amount) VALUES (?, ?, ?) " +
                "ON CONFLICT(month, category) DO UPDATE SET amount=excluded.amount";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, category);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Budget upsert failed: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal get(String month, String category) {
        String sql = "SELECT amount FROM budgets WHERE month=? AND category=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, category);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Budget get failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int delete(String month, String category) {
        String sql = "DELETE FROM budgets WHERE month=? AND category=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, category);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Budget delete failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BudgetRow> list(String month) {
        String sql = "SELECT month, category, amount FROM budgets WHERE month=? ORDER BY category";
        List<BudgetRow> out = new ArrayList<>();
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new BudgetRow(rs.getString(1), rs.getString(2), rs.getBigDecimal(3)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Budget list failed: " + e.getMessage(), e);
        }
        return out;
    }

    @Override
    public Map<String, BigDecimal> spendByCategory(String month) {
        // Sum absolute value of expenses (assuming negative amounts are expenses; adjust if schema uses type)
        String sql = "SELECT category, SUM(CASE WHEN amount < 0 THEN -amount ELSE 0 END) AS spend " +
                "FROM transactions WHERE substr(date,1,7)=? GROUP BY category";
        Map<String, BigDecimal> out = new LinkedHashMap<>();
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.put(rs.getString("category"), rs.getBigDecimal("spend"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Spend aggregation failed: " + e.getMessage(), e);
        }
        return out;
    }
}
