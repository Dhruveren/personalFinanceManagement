package com.acme.pfm.persistence.sqlite;

import com.acme.pfm.persistence.dao.BudgetDao;

import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SqliteBudgetDao implements BudgetDao {
    private final DataSource ds;

    public SqliteBudgetDao(DataSource ds) { this.ds = ds; }

    @Override
    public int upsert(String month, String category, BigDecimal amount) {
        String sql = """
            INSERT INTO budgets(month, category, amount)
            VALUES(?, ?, ?)
            ON CONFLICT(month, category)
            DO UPDATE SET amount = excluded.amount
        """;
        try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, category);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("upsert budget failed", e);
        }
    }

    @Override
    public BigDecimal findAmount(String month, String category) {
        String sql = "SELECT amount FROM budgets WHERE month = ? AND category = ?";
        try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, category);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("find budget failed", e);
        }
    }

    @Override
    public List<Row> listByMonth(String month) {
        String sql = "SELECT category, amount FROM budgets WHERE month = ? ORDER BY category";
        List<Row> rows = new ArrayList<>();
        try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new Row(rs.getString(1), rs.getBigDecimal(2)));
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("list budgets failed", e);
        }
    }

    @Override
    public int delete(String month, String category) {
        String sql = "DELETE FROM budgets WHERE month = ? AND category = ?";
        try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, category);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete budget failed", e);
        }
    }
}
