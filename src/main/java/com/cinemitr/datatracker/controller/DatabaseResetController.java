package com.cinemitr.datatracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class DatabaseResetController {
    
    @Autowired
    private DataSource dataSource;

    @PostMapping("/reset-database")
    public ResponseEntity<?> resetDatabase() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // Drop all tables in the correct order (to handle foreign key constraints)
                String[] dropStatements = {
                    "DROP TABLE IF EXISTS upload_catalog_media_list CASCADE",
                    "DROP TABLE IF EXISTS content_catalog_media_list CASCADE", 
                    "DROP TABLE IF EXISTS upload_catalog CASCADE",
                    "DROP TABLE IF EXISTS content_catalog CASCADE",
                    "DROP TABLE IF EXISTS media_catalog CASCADE",
                    "DROP TABLE IF EXISTS states_catalog CASCADE", 
                    "DROP TABLE IF EXISTS metadata_status CASCADE"
                };
                
                int tablesDropped = 0;
                for (String dropSql : dropStatements) {
                    try {
                        statement.executeUpdate(dropSql);
                        tablesDropped++;
                        System.out.println("Executed: " + dropSql);
                    } catch (Exception e) {
                        System.out.println("Table may not exist: " + dropSql + " - " + e.getMessage());
                    }
                }
                
                response.put("success", true);
                response.put("message", "Database reset completed successfully");
                response.put("tablesDropped", tablesDropped);
                response.put("note", "Application will recreate tables on next entity access");
                
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Database reset failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/truncate-all-tables")
    public ResponseEntity<?> truncateAllTables() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // First disable foreign key checks
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
                
                // Truncate all tables (keeps structure, removes data)
                String[] truncateStatements = {
                    "TRUNCATE TABLE upload_catalog_media_list",
                    "TRUNCATE TABLE content_catalog_media_list",
                    "TRUNCATE TABLE upload_catalog",
                    "TRUNCATE TABLE content_catalog", 
                    "TRUNCATE TABLE media_catalog",
                    "TRUNCATE TABLE states_catalog",
                    "TRUNCATE TABLE metadata_status"
                };
                
                int tablesTruncated = 0;
                for (String truncateSql : truncateStatements) {
                    try {
                        statement.executeUpdate(truncateSql);
                        tablesTruncated++;
                        System.out.println("Executed: " + truncateSql);
                    } catch (Exception e) {
                        System.out.println("Table may not exist: " + truncateSql + " - " + e.getMessage());
                    }
                }
                
                // Re-enable foreign key checks
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
                
                response.put("success", true);
                response.put("message", "All table data truncated successfully");
                response.put("tablesTruncated", tablesTruncated);
                response.put("note", "Table structures preserved, only data cleared");
                
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Truncate operation failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/database-info")
    public ResponseEntity<?> getDatabaseInfo() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // Get table counts
                String[] tables = {"media_catalog", "content_catalog", "upload_catalog", "states_catalog"};
                Map<String, Integer> tableCounts = new HashMap<>();
                
                for (String table : tables) {
                    try {
//                        var rs = statement.executeQuery("SELECT COUNT(*) FROM " + table);
//                        if (rs.next()) {
//                            tableCounts.put(table, rs.getInt(1));
//                        }
                    } catch (Exception e) {
                        tableCounts.put(table, 0);
                    }
                }
                
                response.put("success", true);
                response.put("tableCounts", tableCounts);
                response.put("databaseUrl", connection.getMetaData().getURL());
                
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get database info: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}