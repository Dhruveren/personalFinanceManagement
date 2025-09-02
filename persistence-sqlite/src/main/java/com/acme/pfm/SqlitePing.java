package com.acme.pfm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlitePing {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:pfm.db";
        try (Connection conn = DriverManager.getConnection(url)) { // try-with-resources
            if (conn != null) {
                System.out.println("connected");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
