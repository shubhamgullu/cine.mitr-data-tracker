package com.cinemitr.controller;

import com.cinemitr.service.EnhancedBulkUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Bulk Upload Controller with comprehensive error handling and logging
 */
@Controller
@RequestMapping("/dashboard")
public class EnhancedBulkUploadController {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedBulkUploadController.class);

    @Autowired
    private EnhancedBulkUploadService enhancedBulkUploadService;

    /**
     * Enhanced Content Catalog bulk upload with comprehensive error handling
     */
    @PostMapping("/enhanced-bulk-upload/content-catalog")
    public String enhancedBulkUploadContentCatalog(@RequestParam("file") MultipartFile file,
                                                   RedirectAttributes redirectAttributes) {
        
        logger.info("Enhanced Content Catalog bulk upload initiated - File: {}, Size: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }

            Map<String, Object> result = enhancedBulkUploadService.processContentCatalogBulkUpload(file);
            return processEnhancedBulkUploadResult(result, redirectAttributes, "Content Catalog");

        } catch (Exception e) {
            String errorMsg = "Critical error during Content Catalog bulk upload: " + e.getMessage();
            logger.error("Critical error in enhanced bulk upload for Content Catalog", e);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            redirectAttributes.addFlashAttribute("bulkUploadErrors", Arrays.asList(
                "File processing failed completely",
                "Error: " + e.getMessage(),
                "Please check your file format and data, then try again"
            ));
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Enhanced Media Catalog bulk upload with comprehensive error handling
     */
    @PostMapping("/enhanced-bulk-upload/media-catalog")
    public String enhancedBulkUploadMediaCatalog(@RequestParam("file") MultipartFile file,
                                                 RedirectAttributes redirectAttributes) {
        
        logger.info("Enhanced Media Catalog bulk upload initiated - File: {}, Size: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }

            Map<String, Object> result = enhancedBulkUploadService.processMediaCatalogBulkUpload(file);
            return processEnhancedBulkUploadResult(result, redirectAttributes, "Media Catalog");

        } catch (Exception e) {
            String errorMsg = "Critical error during Media Catalog bulk upload: " + e.getMessage();
            logger.error("Critical error in enhanced bulk upload for Media Catalog", e);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            redirectAttributes.addFlashAttribute("bulkUploadErrors", Arrays.asList(
                "File processing failed completely",
                "Error: " + e.getMessage(),
                "Please check your file format and data, then try again"
            ));
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Enhanced Upload Catalog bulk upload with comprehensive error handling
     */
    @PostMapping("/enhanced-bulk-upload/upload-catalog")
    public String enhancedBulkUploadUploadCatalog(@RequestParam("file") MultipartFile file,
                                                  RedirectAttributes redirectAttributes) {
        
        logger.info("Enhanced Upload Catalog bulk upload initiated - File: {}, Size: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }

            Map<String, Object> result = enhancedBulkUploadService.processUploadCatalogBulkUpload(file);
            return processEnhancedBulkUploadResult(result, redirectAttributes, "Upload Catalog");

        } catch (Exception e) {
            String errorMsg = "Critical error during Upload Catalog bulk upload: " + e.getMessage();
            logger.error("Critical error in enhanced bulk upload for Upload Catalog", e);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            redirectAttributes.addFlashAttribute("bulkUploadErrors", Arrays.asList(
                "File processing failed completely",
                "Error: " + e.getMessage(),
                "Please check your file format and data, then try again"
            ));
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Enhanced States Catalog bulk upload with comprehensive error handling
     */
    @PostMapping("/enhanced-bulk-upload/states-catalog")
    public String enhancedBulkUploadStatesCatalog(@RequestParam("file") MultipartFile file,
                                                  RedirectAttributes redirectAttributes) {
        
        logger.info("Enhanced States Catalog bulk upload initiated - File: {}, Size: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }

            Map<String, Object> result = enhancedBulkUploadService.processStatesCatalogBulkUpload(file);
            return processEnhancedBulkUploadResult(result, redirectAttributes, "States Catalog");

        } catch (Exception e) {
            String errorMsg = "Critical error during States Catalog bulk upload: " + e.getMessage();
            logger.error("Critical error in enhanced bulk upload for States Catalog", e);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            redirectAttributes.addFlashAttribute("bulkUploadErrors", Arrays.asList(
                "File processing failed completely",
                "Error: " + e.getMessage(),
                "Please check your file format and data, then try again"
            ));
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Common method to process and format bulk upload results for UI display
     */
    private String processEnhancedBulkUploadResult(Map<String, Object> result, 
                                                   RedirectAttributes redirectAttributes, 
                                                   String catalogType) {
        
        int successCount = (Integer) result.get("successCount");
        int errorCount = (Integer) result.get("errorCount");
        int warningCount = (Integer) result.get("warningCount");
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        @SuppressWarnings("unchecked")
        List<String> warnings = (List<String>) result.get("warnings");
        String fileName = (String) result.get("fileName");
        Long processingTimeMs = ((Number) result.get("processingTimeMs")).longValue();

        // Log the result summary
        logger.info("Enhanced bulk upload completed - Catalog: {}, File: {}, Success: {}, Errors: {}, Warnings: {}, Time: {}ms", 
                   catalogType, fileName, successCount, errorCount, warningCount, processingTimeMs);

        // Prepare success message
        if (successCount > 0) {
            StringBuilder successMsg = new StringBuilder();
            successMsg.append(String.format("✅ Successfully processed %d %s entries", successCount, catalogType));
            
            if (processingTimeMs != null) {
                successMsg.append(String.format(" in %d ms", processingTimeMs));
            }
            
            // Add additional details from result
            if (result.containsKey("newEntries") && result.containsKey("updatedEntries")) {
                Integer newEntriesObj = (Integer) result.getOrDefault("newEntries", 0);
                Integer updatedEntriesObj = (Integer) result.getOrDefault("updatedEntries", 0);
                int newEntries = newEntriesObj != null ? newEntriesObj : 0;
                int updatedEntries = updatedEntriesObj != null ? updatedEntriesObj : 0;
                if (newEntries > 0 || updatedEntries > 0) {
                    successMsg.append(String.format(" (%d new, %d updated)", newEntries, updatedEntries));
                }
            }
            
            if (result.containsKey("autoCreatedUploadEntries")) {
                Integer autoCreatedObj = (Integer) result.get("autoCreatedUploadEntries");
                int autoCreated = autoCreatedObj != null ? autoCreatedObj : 0;
                if (autoCreated > 0) {
                    successMsg.append(String.format(". %d Upload Catalog entries were auto-created.", autoCreated));
                }
            }
            
            redirectAttributes.addFlashAttribute("success", successMsg.toString());
        }

        // Handle warnings
        if (warningCount > 0 && warnings != null && !warnings.isEmpty()) {
            StringBuilder warningMsg = new StringBuilder("⚠️ Warnings during processing:");
            for (String warning : warnings) {
                warningMsg.append("\n• ").append(warning);
            }
            redirectAttributes.addFlashAttribute("warning", warningMsg.toString());
            redirectAttributes.addFlashAttribute("bulkUploadWarnings", warnings);
        }

        // Handle errors
        if (errorCount > 0) {
            if (successCount == 0) {
                // Complete failure
                redirectAttributes.addFlashAttribute("error", 
                    String.format("❌ %s bulk upload failed! No records were processed successfully. " +
                                 "Found %d errors in file '%s'.", 
                                 catalogType, errorCount, fileName));
            } else {
                // Partial success with errors
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("⚠️ %s bulk upload completed with %d errors. " +
                                 "%d records were processed successfully, but %d failed.", 
                                 catalogType, errorCount, successCount, errorCount));
            }
            
            if (errors != null && !errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("bulkUploadErrors", errors);
                
                // Log detailed errors for debugging
                logger.warn("Bulk upload errors for {} ({}): {}", catalogType, fileName, errors);
            }
        }

        // Add file processing details
        Map<String, Object> bulkUploadDetails = new HashMap<>();
        bulkUploadDetails.put("fileName", fileName);
        bulkUploadDetails.put("fileType", result.getOrDefault("fileType", "unknown"));
        bulkUploadDetails.put("fileSize", result.getOrDefault("fileSize", 0L));
        bulkUploadDetails.put("processingTime", processingTimeMs != null ? processingTimeMs + " ms" : "unknown");
        bulkUploadDetails.put("processedAt", result.getOrDefault("processedAt", "unknown"));
        redirectAttributes.addFlashAttribute("bulkUploadDetails", bulkUploadDetails);

        return "redirect:/dashboard";
    }
}