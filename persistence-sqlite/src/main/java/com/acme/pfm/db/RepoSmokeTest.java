//package com.acme.pfm.db;
//
//import com.acme.pfm.Transaction;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//public class RepoSmokeTest {
//    public static void main(String[] args) throws Exception {
//// 1) Use the exact absolute DB path used in sqlite3
//        String url = "jdbc:sqlite:/Users/debasishsarkar/Developer/SelfFinanceTracker/pfm/pfm.db";
//// 2) Ensure schema exists (optional if already bootstrapped)
//        new SchemaBootstrap(url, "schema.sql").run();
//// 3) Create repository
//        SQLiteTransactionRepository repo = new SQLiteTransactionRepository(url);
//// 4) Create a sample transaction
//        Transaction t = new Transaction("t1", LocalDate.now(), "Coffee", new BigDecimal("-120.50"), "Food");
//// 5) Save
//        repo.save(t);
//// 6) Read back
//        System.out.println("Found: " + repo.findById("t1"));
//// 7) Clean up
//        System.out.println("Deleted? " + repo.deleteById("t1"));
//    }
//}
//
