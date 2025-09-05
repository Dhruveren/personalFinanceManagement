package com.acme.pfm.db;

import com.acme.pfm.core.TransactionRepositoryPort;
import com.acme.pfm.core.TransactionRepositoryPort.TxRow;

import java.sql.*;
import java.util.*;

public class SQLiteTransactionRepository implements TransactionRepositoryPort {

    private final String url; // jdbc:sqlite:/absolute/path/pfm.db

    public SQLiteTransactionRepository(String jdbcUrl) {
        this.url = jdbcUrl;
    }

    private Connection conn() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public List<TxRow> find(String month, String category, Double minAmount, Double maxAmount, String type, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT date, amount, description, category, type FROM transactions WHERE 1=1"
        );
        List<Object> args = new ArrayList<>();

        if (month != null && !month.isBlank()) {
            sql.append(" AND substr(date,1,7) = ?");
            args.add(month); // YYYY-MM
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            args.add(category);
        }
        if (type != null && !type.isBlank()) {
            sql.append(" AND lower(type) = ?");
            args.add(type.toLowerCase());
        }
        if (minAmount != null) {
            sql.append(" AND amount >= ?");
            args.add(minAmount);
        }
        if (maxAmount != null) {
            sql.append(" AND amount <= ?");
            args.add(maxAmount);
        }
        sql.append(" ORDER BY date DESC, rowid DESC");
        sql.append(" LIMIT ").append(Math.max(1, limit));

        List<TxRow> out = new ArrayList<>();
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) ps.setObject(i + 1, args.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getString("date"); // store ISO text in DB
                    double amount = rs.getDouble("amount");
                    String desc = rs.getString("description");
                    String cat = rs.getString("category");
                    String ty = rs.getString("type");
                    out.add(new TxRow(date, amount, desc, cat, ty));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("find() failed: " + e.getMessage(), e);
        }
        return out;
    }

    @Override
    public int insert(String date, double amount, String description, String category, String type) {
        String sql = "INSERT INTO transactions(date, amount, description, category, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setDouble(2, amount);
            ps.setString(3, description);
            ps.setString(4, category);
            ps.setString(5, type.toLowerCase());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insert() failed: " + e.getMessage(), e);
        }
    }

    @Override
    public double sumByType(String period, String month, String type) {
        // period can be "monthly","weekly","yearly"; for now use month if given
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(amount),0) AS total FROM transactions WHERE lower(type)=?");
        List<Object> args = new ArrayList<>();
        args.add(type.toLowerCase());

        if (month != null && !month.isBlank()) {
            sql.append(" AND substr(date,1,7)=?");
            args.add(month);
        }
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) ps.setObject(i + 1, args.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("sumByType() failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int count(String period, String month) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS cnt FROM transactions WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (month != null && !month.isBlank()) {
            sql.append(" AND substr(date,1,7)=?");
            args.add(month);
        }
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) ps.setObject(i + 1, args.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("count() failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Double> sumByCategory(String period, String month) {
        StringBuilder sql = new StringBuilder(
                "SELECT category, COALESCE(SUM(amount),0) AS total FROM transactions WHERE 1=1"
        );
        List<Object> args = new ArrayList<>();
        if (month != null && !month.isBlank()) {
            sql.append(" AND substr(date,1,7)=?");
            args.add(month);
        }
        sql.append(" GROUP BY category ORDER BY total DESC");

        Map<String, Double> result = new LinkedHashMap<>();
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) ps.setObject(i + 1, args.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("category"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("sumByCategory() failed: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<String> categories() {
        String sql = "SELECT DISTINCT category FROM transactions ORDER BY category";
        List<String> cats = new ArrayList<>();
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cats.add(rs.getString(1));
        } catch (SQLException e) {
            throw new RuntimeException("categories() failed: " + e.getMessage(), e);
        }
        return cats;
    }
}
