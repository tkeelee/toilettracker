package com.toilet.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {
    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        dbManager = new DatabaseManager();
        dbManager.clearDatabase();
    }

    @Test
    void testSaveAndRetrieveRecord() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(5);
        long duration = 300; // 5 minutes in seconds

        dbManager.saveRecord(startTime, endTime, duration);

        int currentYear = startTime.getYear();
        Map<String, Double> stats = dbManager.getMonthlyStats(currentYear);
        
        assertFalse(stats.isEmpty(), "Statistics should not be empty after saving a record");
        String monthKey = String.format("%d-%02d", currentYear, startTime.getMonthValue());
        assertTrue(stats.containsKey(monthKey), "Statistics should contain the current month");
        assertEquals(duration, stats.get(monthKey), "Average duration should match the saved duration");
    }

    @Test
    void testMonthlyStatsCalculation() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(10);
        long duration = 600; // 10 minutes in seconds

        // Save multiple records
        dbManager.saveRecord(startTime, endTime, duration);
        dbManager.saveRecord(startTime, endTime, duration);

        int currentYear = startTime.getYear();
        Map<String, Double> stats = dbManager.getMonthlyStats(currentYear);
        
        String monthKey = String.format("%d-%02d", currentYear, startTime.getMonthValue());
        assertEquals(600.0, stats.get(monthKey), "Average duration should be 600 seconds");
    }
}