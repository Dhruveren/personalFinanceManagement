package com.acme.pfm.db;

import com.acme.pfm.Transaction;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;


import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteTransactionRepositoryTest {


    private String url;

    @BeforeEach
    void setup(@TempDir Path tempDir) throws Exception {
        Path dbFile = tempDir.resolve("test.db");
        url = "jdbc:sqlite:" + dbFile.toAbsolutePath();
        new SchemaBootstrap(url, "schema.sql").run();
    }

    @Test
    void save_find_update_delete_roundTrip_ok() throws Exception {
        var repo = new SQLiteTransactionRepository(url);
        var t = new Transaction("t1", LocalDate.parse("2025-09-05"), "Coffee", new BigDecimal("-120.50"), "Food");

        repo.save(t);
        var found = repo.findById("t1");
        assertNotNull(found);
        assertEquals(t.getId(), found.getId());
        assertEquals(t.getAmount(), found.getAmount());

        var updated = new Transaction("t1", t.getDate(), "Coffee @ Starbucks", t.getAmount(), t.getCategory());
        assertTrue(repo.update(updated));
        assertEquals("Coffee @ Starbucks", repo.findById("t1").getDescription());

        assertTrue(repo.deleteById("t1"));
        assertNull(repo.findById("t1"));
    }

    @Test
    void saveAll_and_queries_ok() throws Exception {
        var repo = new SQLiteTransactionRepository(url);
        var t1 = new Transaction("a1", LocalDate.parse("2025-09-01"), "Grocery", new BigDecimal("100.00"), "Food");
        var t2 = new Transaction("a2", LocalDate.parse("2025-09-02"), "Metro", new BigDecimal("50.00"), "Travel");
        var t3 = new Transaction("a3", LocalDate.parse("2025-09-03"), "Dinner", new BigDecimal("200.00"), "Food");

        int[] counts = repo.saveAll(List.of(t1, t2, t3));
        assertEquals(3, counts.length);

        var foods = repo.findByCategory("Food");
        assertEquals(2, foods.size());

        var range = repo.findByDateRange(LocalDate.parse("2025-09-02"), LocalDate.parse("2025-09-03"));
        assertEquals(2, range.size());
        assertEquals("a2", range.get(0).getId());
    }
}