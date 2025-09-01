package com.cinemitr.service;

import com.cinemitr.model.ContentCatalog;
import com.cinemitr.repository.ContentCatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for validating bulk upload data with special focus on link uniqueness
 * and duplicate detection for content catalog entries
 */
@Service
public class BulkUploadValidationService {

    private static final Logger logger = LoggerFactory.getLogger(BulkUploadValidationService.class);

    @Autowired
    private ContentCatalogRepository contentCatalogRepository;

    /**
     * Comprehensive validation result for bulk upload operations
     */
    public static class ValidationResult {
        private boolean isValid;
        private List<String> errors;
        private List<String> warnings;
        private List<String> duplicateLinks;
        private List<String> existingLinks;
        private Map<String, Integer> duplicateCount;

        public ValidationResult() {
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
            this.duplicateLinks = new ArrayList<>();
            this.existingLinks = new ArrayList<>();
            this.duplicateCount = new HashMap<>();
        }

        // Getters and setters
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }

        public List<String> getDuplicateLinks() { return duplicateLinks; }
        public void setDuplicateLinks(List<String> duplicateLinks) { this.duplicateLinks = duplicateLinks; }

        public List<String> getExistingLinks() { return existingLinks; }
        public void setExistingLinks(List<String> existingLinks) { this.existingLinks = existingLinks; }

        public Map<String, Integer> getDuplicateCount() { return duplicateCount; }
        public void setDuplicateCount(Map<String, Integer> duplicateCount) { this.duplicateCount = duplicateCount; }

        public void addError(String error) {
            this.errors.add(error);
            this.isValid = false;
        }

        public void addWarning(String warning) {
            this.warnings.add(warning);
        }

        public int getTotalIssues() {
            return errors.size() + warnings.size();
        }
    }

    /**
     * Validates a list of ContentCatalog entities for bulk upload
     * with comprehensive duplicate link checking
     */
    public ValidationResult validateContentCatalogList(List<ContentCatalog> contentCatalogs) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        if (contentCatalogs == null || contentCatalogs.isEmpty()) {
            result.addError("No content catalog entries to validate");
            return result;
        }

        logger.info("Starting validation for {} content catalog entries", contentCatalogs.size());

        // Step 1: Validate individual entries
        validateIndividualEntries(contentCatalogs, result);

        // Step 2: Check for duplicates within the upload batch
        checkInternalDuplicates(contentCatalogs, result);

        // Step 3: Check for duplicates against existing database entries
        checkDatabaseDuplicates(contentCatalogs, result);

        // Step 4: Additional business logic validation
        validateBusinessRules(contentCatalogs, result);

        logger.info("Validation completed - Valid: {}, Errors: {}, Warnings: {}", 
                   result.isValid(), result.getErrors().size(), result.getWarnings().size());

        return result;
    }

    /**
     * Validates individual ContentCatalog entries for required fields and data integrity
     */
    private void validateIndividualEntries(List<ContentCatalog> contentCatalogs, ValidationResult result) {
        for (int i = 0; i < contentCatalogs.size(); i++) {
            ContentCatalog catalog = contentCatalogs.get(i);
            int rowNumber = i + 1; // For user-friendly row numbering

            // Validate link (primary key)
            if (catalog.getLink() == null || catalog.getLink().trim().isEmpty()) {
                result.addError(String.format("Row %d: Link is required and cannot be empty", rowNumber));
                continue;
            }

            String link = catalog.getLink().trim();
            
            // Validate link format
            if (!isValidUrl(link)) {
                result.addError(String.format("Row %d: Invalid URL format for link '%s'", rowNumber, link));
            }

            // Validate link length
            if (link.length() > 500) {
                result.addError(String.format("Row %d: Link exceeds maximum length of 500 characters", rowNumber));
            }

            // Validate required fields
            if (catalog.getMediaCatalogType() == null) {
                result.addError(String.format("Row %d: Media catalog type is required", rowNumber));
            }

            if (catalog.getMediaCatalogName() == null || catalog.getMediaCatalogName().trim().isEmpty()) {
                result.addError(String.format("Row %d: Media catalog name is required", rowNumber));
            }

            if (catalog.getStatus() == null) {
                result.addError(String.format("Row %d: Status is required", rowNumber));
            }

            // Validate optional field constraints
            if (catalog.getPriority() == null) {
                catalog.setPriority(ContentCatalog.Priority.MEDIUM); // Set default
                result.addWarning(String.format("Row %d: Priority not specified, defaulting to MEDIUM", rowNumber));
            }
        }
    }

    /**
     * Checks for duplicate links within the current upload batch
     */
    private void checkInternalDuplicates(List<ContentCatalog> contentCatalogs, ValidationResult result) {
        Map<String, List<Integer>> linkToRowNumbers = new HashMap<>();

        for (int i = 0; i < contentCatalogs.size(); i++) {
            ContentCatalog catalog = contentCatalogs.get(i);
            if (catalog.getLink() != null && !catalog.getLink().trim().isEmpty()) {
                String link = catalog.getLink().trim();
                linkToRowNumbers.computeIfAbsent(link, k -> new ArrayList<>()).add(i + 1);
            }
        }

        // Find duplicates
        linkToRowNumbers.forEach((link, rowNumbers) -> {
            if (rowNumbers.size() > 1) {
                result.getDuplicateLinks().add(link);
                result.getDuplicateCount().put(link, rowNumbers.size());
                
                String rowNumbersStr = rowNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
                
                result.addError(String.format(
                    "Duplicate link found in upload file: '%s' appears in rows [%s]. " +
                    "Each link must be unique as it serves as the primary key.",
                    link, rowNumbersStr));
            }
        });

        if (!result.getDuplicateLinks().isEmpty()) {
            result.addError(String.format(
                "Found %d unique links with duplicates in the upload file. " +
                "Please remove duplicate entries before proceeding.",
                result.getDuplicateLinks().size()));
        }
    }

    /**
     * Checks for duplicate links against existing database entries
     */
    private void checkDatabaseDuplicates(List<ContentCatalog> contentCatalogs, ValidationResult result) {
        List<String> linksToCheck = contentCatalogs.stream()
            .filter(catalog -> catalog.getLink() != null && !catalog.getLink().trim().isEmpty())
            .map(catalog -> catalog.getLink().trim())
            .distinct()
            .collect(Collectors.toList());

        if (linksToCheck.isEmpty()) {
            return;
        }

        try {
            // Check which links already exist in database
            List<String> existingLinks = new ArrayList<>();
            for (String link : linksToCheck) {
                if (contentCatalogRepository.existsByLink(link)) {
                    existingLinks.add(link);
                    result.getExistingLinks().add(link);
                }
            }

            if (!existingLinks.isEmpty()) {
                result.addError(String.format(
                    "Found %d links that already exist in the database: %s. " +
                    "Links must be unique across all content catalog entries. " +
                    "Please use different links or update the existing entries instead.",
                    existingLinks.size(),
                    existingLinks.stream()
                        .limit(5) // Show only first 5 to avoid overly long error messages
                        .collect(Collectors.joining(", ")) + 
                    (existingLinks.size() > 5 ? "..." : "")));

                // Add individual row-level errors for existing links
                for (int i = 0; i < contentCatalogs.size(); i++) {
                    ContentCatalog catalog = contentCatalogs.get(i);
                    if (catalog.getLink() != null && existingLinks.contains(catalog.getLink().trim())) {
                        result.addError(String.format(
                            "Row %d: Link '%s' already exists in database and cannot be duplicated",
                            i + 1, catalog.getLink().trim()));
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error checking for duplicate links in database", e);
            result.addWarning("Could not check for existing links in database. Proceeding with caution.");
        }
    }

    /**
     * Additional business logic validation
     */
    private void validateBusinessRules(List<ContentCatalog> contentCatalogs, ValidationResult result) {
        // Check for suspicious patterns
        Map<String, Long> mediaTypeCount = contentCatalogs.stream()
            .filter(catalog -> catalog.getMediaCatalogType() != null)
            .collect(Collectors.groupingBy(
                catalog -> catalog.getMediaCatalogType().name(),
                Collectors.counting()));

        // Warn if upload contains only one type of media (might be intentional)
        if (mediaTypeCount.size() == 1 && contentCatalogs.size() > 10) {
            String mediaType = mediaTypeCount.keySet().iterator().next();
            result.addWarning(String.format(
                "All %d entries are of type '%s'. Please verify this is intentional.",
                contentCatalogs.size(), mediaType));
        }

        // Check for unusual link patterns
        long httpsCount = contentCatalogs.stream()
            .filter(catalog -> catalog.getLink() != null && catalog.getLink().startsWith("https://"))
            .count();
        
        if (httpsCount < contentCatalogs.size() * 0.8) { // Less than 80% HTTPS
            result.addWarning(String.format(
                "Only %d out of %d links use HTTPS. Consider using secure links for better security.",
                httpsCount, contentCatalogs.size()));
        }
    }

    /**
     * Basic URL validation
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        url = url.trim().toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://");
    }

    /**
     * Validates and filters out invalid entries, returning only valid ones
     * along with detailed error information
     */
    public ValidationResult validateAndFilter(List<ContentCatalog> contentCatalogs) {
        ValidationResult result = validateContentCatalogList(contentCatalogs);
        
        if (!result.isValid()) {
            logger.warn("Validation failed with {} errors and {} warnings", 
                       result.getErrors().size(), result.getWarnings().size());
            
            // Log detailed validation issues for debugging
            result.getErrors().forEach(error -> logger.warn("Validation Error: {}", error));
            result.getWarnings().forEach(warning -> logger.info("Validation Warning: {}", warning));
        }
        
        return result;
    }

    /**
     * Quick check if a single link already exists in database
     */
    public boolean linkExists(String link) {
        if (link == null || link.trim().isEmpty()) {
            return false;
        }
        
        try {
            return contentCatalogRepository.existsByLink(link.trim());
        } catch (Exception e) {
            logger.error("Error checking if link exists: {}", link, e);
            return false; // Assume it doesn't exist if we can't check
        }
    }

    /**
     * Get detailed information about existing links
     */
    public Map<String, ContentCatalog> getExistingLinksInfo(List<String> links) {
        Map<String, ContentCatalog> existingLinksMap = new HashMap<>();
        
        for (String link : links) {
            if (link != null && !link.trim().isEmpty()) {
                try {
                    Optional<ContentCatalog> existing = contentCatalogRepository.findByLink(link.trim());
                    existing.ifPresent(catalog -> existingLinksMap.put(link.trim(), catalog));
                } catch (Exception e) {
                    logger.warn("Error retrieving existing link info for: {}", link, e);
                }
            }
        }
        
        return existingLinksMap;
    }
}