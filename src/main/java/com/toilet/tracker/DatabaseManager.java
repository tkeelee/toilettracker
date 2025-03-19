package com.toilet.tracker;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:toilet_tracker.db";

    public DatabaseManager() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            String sql = "CREATE TABLE IF NOT EXISTS records (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "start_time TEXT NOT NULL," +
                    "end_time TEXT NOT NULL," +
                    "duration INTEGER NOT NULL" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM records");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    public void saveRecord(LocalDateTime startTime, LocalDateTime endTime, long duration) {
        String sql = "INSERT INTO records (start_time, end_time, duration) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startTime.toString());
            pstmt.setString(2, endTime.toString());
            pstmt.setLong(3, duration);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save record", e);
        }
    }

    public Map<String, Double> getMonthlyStats(int year) {
        Map<String, Double> stats = new TreeMap<>();
        String sql = "SELECT strftime('%Y-%m', start_time) as month, AVG(duration) as avg_duration " +
                    "FROM records " +
                    "WHERE strftime('%Y', start_time) = ? " +
                    "GROUP BY month";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                stats.put(rs.getString("month"), rs.getDouble("avg_duration"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get monthly stats", e);
        }
        return stats;
    }
}