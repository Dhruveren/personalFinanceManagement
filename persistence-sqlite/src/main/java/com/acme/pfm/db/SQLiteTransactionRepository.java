package com.acme.pfm.db;

import com.acme.pfm.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTransactionRepository implements TransactionRepository {
    private final String url;

    public SQLiteTransactionRepository(String url) {
        this.url = url;
    }

    @Override
    public void save(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions(id,date,description,amount,category) VALUES(?,?,?,?,?)";
        try (Connection c = DriverManager.getConnection(url)) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, t.getId());
                ps.setString(2, t.getDate().toString());
                ps.setString(3, t.getDescription());
                ps.setString(4, t.getAmount().toPlainString());
                ps.setString(5, t.getCategory());
                ps.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    @Override
    public Transaction findById(String id) throws SQLException {
        String sql = "SELECT id,date,description,amount,category FROM transactions WHERE id=?";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT id,date,description,amount,category FROM transactions ORDER BY date DESC";
        List<Transaction> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    @Override
    public boolean update(Transaction t) throws SQLException {
        String sql = "UPDATE transactions SET date=?, description=?, amount=?, category=? WHERE id=?";
        try (Connection c = DriverManager.getConnection(url)) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, t.getDate().toString());
                ps.setString(2, t.getDescription());
                ps.setString(3, t.getAmount().toPlainString());
                ps.setString(4, t.getCategory());
                ps.setString(5, t.getId());
                int n = ps.executeUpdate();
                c.commit();
                return n > 0;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean deleteById(String id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id=?";
        try (Connection c = DriverManager.getConnection(url)) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, id);
                int n = ps.executeUpdate();
                c.commit();
                return n > 0;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        LocalDate date = LocalDate.parse(rs.getString("date"));
        String description = rs.getString("description");
        BigDecimal amount = new BigDecimal(rs.getString("amount"));
        String category = rs.getString("category");
        return new Transaction(id, date, description, amount, category);
    }

    public int[] saveAll(java.util.List<Transaction> items) throws java.sql.SQLException {
        String sql = "INSERT INTO transactions(id,date,description,amount,category) VALUES(?,?,?,?,?)";
        try (java.sql.Connection c = java.sql.DriverManager.getConnection(url);
             java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            c.setAutoCommit(false);
            for (Transaction t : items) {
                ps.setString(1, t.getId());
                ps.setString(2, t.getDate().toString()); // ISO yyyy-MM-dd TEXT
                ps.setString(3, t.getDescription());
                ps.setString(4, t.getAmount().toPlainString());
                ps.setString(5, t.getCategory());
                ps.addBatch();
            }
            int[] counts;
            try {
                counts = ps.executeBatch();
                c.commit();
            } catch (java.sql.SQLException e) {
                c.rollback();
                throw e;
            }
            return counts;
        }
    }

    public java.util.List<Transaction> findByCategory(String category) throws java.sql.SQLException {
        String sql = "SELECT id,date,description,amount,category FROM transactions WHERE category=? ORDER BY date DESC";
        java.util.List<Transaction> out = new java.util.ArrayList<>();
        try (var c = java.sql.DriverManager.getConnection(url);
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, category);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    public java.util.List<Transaction> findByDateRange(java.time.LocalDate start, java.time.LocalDate end) throws java.sql.SQLException {
// dates stored as ISO yyyy-MM-dd TEXT, so lexicographic BETWEEN works
        String sql = "SELECT id,date,description,amount,category FROM transactions WHERE date BETWEEN ? AND ? ORDER BY date ASC";
        java.util.List<Transaction> out = new java.util.ArrayList<>();
        try (var c = java.sql.DriverManager.getConnection(url);
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, start.toString());
            ps.setString(2, end.toString());
            try (var rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }


}