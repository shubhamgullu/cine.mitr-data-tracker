package com.cinemitr.service;

import com.cinemitr.model.ContentCatalog;
import com.cinemitr.model.MediaCatalog;
import com.cinemitr.model.StatesCatalog;
import com.cinemitr.model.UploadCatalog;
import com.cinemitr.repository.ContentCatalogRepository;
import com.cinemitr.repository.MediaCatalogRepository;
import com.cinemitr.repository.StatesCatalogRepository;
import com.cinemitr.repository.UploadCatalogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Enhanced Bulk Upload Service with comprehensive error handling and logging
 */
@Service
public class EnhancedBulkUploadService {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedBulkUploadService.class);

    @Autowired
    private ContentCatalogRepository contentCatalogRepository;

    @Autowired
    private UploadCatalogRepository uploadCatalogRepository;

    @Autowired
    private MediaCatalogRepository mediaCatalogRepository;

    @Autowired
    private StatesCatalogRepository statesCatalogRepository;

    /**
     * Enhanced result structure for bulk upload operations
     */
    public static class BulkUploadResult {
        private int successCount;
        private int errorCount;
        private int warningCount;
        private List<String> errors;
        private List<String> warnings;
        private String fileName;
        private String fileType;
        private long fileSize;
        private LocalDateTime processedAt;
        private long processingTimeMs;
        private Map<String, Object> additionalData;

        // Constructors
        public BulkUploadResult() {
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
            this.additionalData = new HashMap<>();
            this.processedAt = LocalDateTime.now();
        }

        // Getters and Setters
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }

        public int getWarningCount() { return warningCount; }
        public void setWarningCount(int warningCount) { this.warningCount = warningCount; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public String getFileType() { return fileType; }
        public void setFileType(String fileType) { this.fileType = fileType; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

        public Map<String, Object> getAdditionalData() { return additionalData; }
        public void setAdditionalData(Map<String, Object> additionalData) { this.additionalData = additionalData; }

        public void addError(String error) {
            this.errors.add(error);
            this.errorCount = this.errors.size();
        }

        public void addWarning(String warning) {
            this.warnings.add(warning);
            this.warningCount = this.warnings.size();
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("successCount", successCount);
            map.put("errorCount", errorCount);
            map.put("warningCount", warningCount);
            map.put("errors", errors);
            map.put("warnings", warnings);
            map.put("fileName", fileName);
            map.put("fileType", fileType);
            map.put("fileSize", fileSize);
            map.put("processedAt", processedAt.toString());
            map.put("processingTimeMs", processingTimeMs);
            map.putAll(additionalData);
            return map;
        }
    }

    /**
     * Enhanced Content Catalog bulk upload with comprehensive error handling
     */
    public Map<String, Object> processContentCatalogBulkUpload(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        BulkUploadResult result = new BulkUploadResult();
        result.setFileName(file.getOriginalFilename());
        result.setFileSize(file.getSize());
        result.setFileType(getFileExtension(file.getOriginalFilename()));

        List<ContentCatalog> successList = new ArrayList<>();

        logger.info("Starting Content Catalog bulk upload - File: {}, Size: {} bytes, Type: {}", 
                   result.getFileName(), result.getFileSize(), result.getFileType());

        try {
            // Validate file
            validateFile(file, result);
            if (result.getErrorCount() > 0) {
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Process file based on type
            String fileName = file.getOriginalFilename().toLowerCase();
            if (fileName.endsWith(".csv")) {
                processContentCatalogCSVEnhanced(file, successList, result);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processContentCatalogExcelEnhanced(file, successList, result);
            } else if (fileName.endsWith(".json")) {
                processContentCatalogJSONEnhanced(file, successList, result);
            } else {
                result.addError("Unsupported file format. Please use CSV, Excel, or JSON files.");
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Save successful entries
            saveContentCatalogEntries(successList, result);

        } catch (Exception e) {
            String errorMsg = "Critical error processing Content Catalog bulk upload: " + e.getMessage();
            result.addError(errorMsg);
            logger.error("Critical error processing Content Catalog bulk upload file: {}", result.getFileName(), e);
        }

        return finalizeBulkUploadResult(result, successList, startTime);
    }

    /**
     * Enhanced Media Catalog bulk upload with comprehensive error handling
     */
    public Map<String, Object> processMediaCatalogBulkUpload(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        BulkUploadResult result = new BulkUploadResult();
        result.setFileName(file.getOriginalFilename());
        result.setFileSize(file.getSize());
        result.setFileType(getFileExtension(file.getOriginalFilename()));

        List<MediaCatalog> successList = new ArrayList<>();

        logger.info("Starting Media Catalog bulk upload - File: {}, Size: {} bytes, Type: {}", 
                   result.getFileName(), result.getFileSize(), result.getFileType());

        try {
            // Validate file
            validateFile(file, result);
            if (result.getErrorCount() > 0) {
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Process file based on type
            String fileName = file.getOriginalFilename().toLowerCase();
            if (fileName.endsWith(".csv")) {
                processMediaCatalogCSVEnhanced(file, successList, result);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processMediaCatalogExcelEnhanced(file, successList, result);
            } else if (fileName.endsWith(".json")) {
                processMediaCatalogJSONEnhanced(file, successList, result);
            } else {
                result.addError("Unsupported file format. Please use CSV, Excel, or JSON files.");
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Save successful entries
            saveMediaCatalogEntries(successList, result);

        } catch (Exception e) {
            String errorMsg = "Critical error processing Media Catalog bulk upload: " + e.getMessage();
            result.addError(errorMsg);
            logger.error("Critical error processing Media Catalog bulk upload file: {}", result.getFileName(), e);
        }

        return finalizeBulkUploadResult(result, successList, startTime);
    }

    /**
     * Enhanced Upload Catalog bulk upload with comprehensive error handling
     */
    public Map<String, Object> processUploadCatalogBulkUpload(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        BulkUploadResult result = new BulkUploadResult();
        result.setFileName(file.getOriginalFilename());
        result.setFileSize(file.getSize());
        result.setFileType(getFileExtension(file.getOriginalFilename()));

        List<UploadCatalog> successList = new ArrayList<>();

        logger.info("Starting Upload Catalog bulk upload - File: {}, Size: {} bytes, Type: {}", 
                   result.getFileName(), result.getFileSize(), result.getFileType());

        try {
            // Validate file
            validateFile(file, result);
            if (result.getErrorCount() > 0) {
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Process file based on type
            String fileName = file.getOriginalFilename().toLowerCase();
            if (fileName.endsWith(".csv")) {
                processUploadCatalogCSVEnhanced(file, successList, result);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processUploadCatalogExcelEnhanced(file, successList, result);
            } else if (fileName.endsWith(".json")) {
                processUploadCatalogJSONEnhanced(file, successList, result);
            } else {
                result.addError("Unsupported file format. Please use CSV, Excel, or JSON files.");
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Save successful entries
            saveUploadCatalogEntries(successList, result);

        } catch (Exception e) {
            String errorMsg = "Critical error processing Upload Catalog bulk upload: " + e.getMessage();
            result.addError(errorMsg);
            logger.error("Critical error processing Upload Catalog bulk upload file: {}", result.getFileName(), e);
        }

        return finalizeBulkUploadResult(result, successList, startTime);
    }

    /**
     * Enhanced States Catalog bulk upload with comprehensive error handling
     */
    public Map<String, Object> processStatesCatalogBulkUpload(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        BulkUploadResult result = new BulkUploadResult();
        result.setFileName(file.getOriginalFilename());
        result.setFileSize(file.getSize());
        result.setFileType(getFileExtension(file.getOriginalFilename()));

        List<StatesCatalog> successList = new ArrayList<>();

        logger.info("Starting States Catalog bulk upload - File: {}, Size: {} bytes, Type: {}", 
                   result.getFileName(), result.getFileSize(), result.getFileType());

        try {
            // Validate file
            validateFile(file, result);
            if (result.getErrorCount() > 0) {
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Process file based on type
            String fileName = file.getOriginalFilename().toLowerCase();
            if (fileName.endsWith(".csv")) {
                processStatesCatalogCSVEnhanced(file, successList, result);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processStatesCatalogExcelEnhanced(file, successList, result);
            } else if (fileName.endsWith(".json")) {
                processStatesCatalogJSONEnhanced(file, successList, result);
            } else {
                result.addError("Unsupported file format. Please use CSV, Excel, or JSON files.");
                return finalizeBulkUploadResult(result, successList, startTime);
            }

            // Save successful entries
            saveStatesCatalogEntries(successList, result);

        } catch (Exception e) {
            String errorMsg = "Critical error processing States Catalog bulk upload: " + e.getMessage();
            result.addError(errorMsg);
            logger.error("Critical error processing States Catalog bulk upload file: {}", result.getFileName(), e);
        }

        return finalizeBulkUploadResult(result, successList, startTime);
    }

    // ================== PRIVATE HELPER METHODS ==================

    private void validateFile(MultipartFile file, BulkUploadResult result) {
        if (file.isEmpty()) {
            result.addError("File is empty. Please select a valid file to upload.");
            return;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            result.addError("Invalid file name. Please provide a valid file.");
            return;
        }

        // Check file size (limit to 50MB)
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            result.addError(String.format("File size too large (%d bytes). Maximum allowed size is %d bytes (50MB).", 
                          file.getSize(), maxSize));
            return;
        }

        // Validate file extension
        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!Arrays.asList("csv", "xlsx", "xls", "json").contains(fileExtension)) {
            result.addError("Unsupported file format: " + fileExtension + ". Supported formats: CSV, Excel (.xlsx, .xls), JSON");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private <T> Map<String, Object> finalizeBulkUploadResult(BulkUploadResult result, List<T> successList, long startTime) {
        result.setSuccessCount(successList.size());
        result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        
        logger.info("Bulk upload completed - File: {}, Success: {}, Errors: {}, Warnings: {}, Time: {}ms", 
                   result.getFileName(), result.getSuccessCount(), result.getErrorCount(), 
                   result.getWarningCount(), result.getProcessingTimeMs());
        
        return result.toMap();
    }

    // Enhanced processing methods (simplified versions - would implement full logic)
    private void processContentCatalogCSVEnhanced(MultipartFile file, List<ContentCatalog> successList, BulkUploadResult result) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                result.addError("CSV file is empty or contains no data rows.");
                return;
            }

            if (records.size() == 1) {
                result.addWarning("CSV file appears to contain only a header row.");
                return;
            }
            
            // Skip header row and process data rows
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                try {
                    ContentCatalog contentCatalog = parseContentCatalogFromCSVRecord(record, i + 1);
                    successList.add(contentCatalog);
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", i + 1, e.getMessage()));
                    logger.warn("Error processing CSV row {} in file {}: {}", i + 1, result.getFileName(), e.getMessage());
                }
            }
            
        } catch (CsvException e) {
            result.addError("CSV parsing error: " + e.getMessage());
            logger.error("CSV parsing error for file {}: {}", result.getFileName(), e.getMessage(), e);
        } catch (IOException e) {
            result.addError("File reading error: " + e.getMessage());
            logger.error("File reading error for file {}: {}", result.getFileName(), e.getMessage(), e);
        }
    }

    private void processContentCatalogExcelEnhanced(MultipartFile file, List<ContentCatalog> successList, BulkUploadResult result) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            if (sheet.getLastRowNum() <= 0) {
                result.addError("Excel file is empty or contains no data rows.");
                return;
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) {
                    result.addWarning(String.format("Row %d is empty, skipping.", i + 1));
                    continue;
                }
                
                try {
                    ContentCatalog contentCatalog = parseContentCatalogFromExcelRow(row, i + 1);
                    successList.add(contentCatalog);
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", i + 1, e.getMessage()));
                    logger.warn("Error processing Excel row {} in file {}: {}", i + 1, result.getFileName(), e.getMessage());
                }
            }
            
        } catch (IOException e) {
            result.addError("Excel file reading error: " + e.getMessage());
            logger.error("Excel file reading error for file {}: {}", result.getFileName(), e.getMessage(), e);
        }
    }

    private void processContentCatalogJSONEnhanced(MultipartFile file, List<ContentCatalog> successList, BulkUploadResult result) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> records = mapper.readValue(file.getInputStream(), new TypeReference<List<Map<String, Object>>>(){});
            
            if (records.isEmpty()) {
                result.addError("JSON file is empty or contains no records.");
                return;
            }
            
            for (int i = 0; i < records.size(); i++) {
                try {
                    ContentCatalog contentCatalog = parseContentCatalogFromJSONRecord(records.get(i), i + 1);
                    successList.add(contentCatalog);
                } catch (Exception e) {
                    result.addError(String.format("Record %d: %s", i + 1, e.getMessage()));
                    logger.warn("Error processing JSON record {} in file {}: {}", i + 1, result.getFileName(), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            result.addError("JSON parsing error: " + e.getMessage());
            logger.error("JSON parsing error for file {}: {}", result.getFileName(), e.getMessage(), e);
        }
    }

    // Similar enhanced methods for other catalog types would follow...
    private void processMediaCatalogCSVEnhanced(MultipartFile file, List<MediaCatalog> successList, BulkUploadResult result) {
        try (InputStreamReader isr = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(isr)) {
            
            String[] headers = csvReader.readNext();
            if (headers == null) {
                result.addError("CSV file is empty");
                return;
            }
            
            String[] row;
            int rowNumber = 1;
            
            while ((row = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    MediaCatalog mediaCatalog = parseMediaCatalogFromCSVRow(row, rowNumber);
                    if (mediaCatalog != null) {
                        successList.add(mediaCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", rowNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing CSV file: " + e.getMessage());
        }
    }

    private void processMediaCatalogExcelEnhanced(MultipartFile file, List<MediaCatalog> successList, BulkUploadResult result) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 0;
            
            for (Row row : sheet) {
                rowNumber++;
                if (rowNumber == 1) continue; // Skip header row
                
                try {
                    MediaCatalog mediaCatalog = parseMediaCatalogFromExcelRow(row, rowNumber);
                    if (mediaCatalog != null) {
                        successList.add(mediaCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", rowNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing Excel file: " + e.getMessage());
        }
    }

    private void processMediaCatalogJSONEnhanced(MultipartFile file, List<MediaCatalog> successList, BulkUploadResult result) {
        try (InputStreamReader isr = new InputStreamReader(file.getInputStream())) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonArray = objectMapper.readTree(isr);
            
            if (!jsonArray.isArray()) {
                result.addError("JSON file must contain an array of objects");
                return;
            }
            
            int recordNumber = 0;
            for (JsonNode jsonNode : jsonArray) {
                recordNumber++;
                try {
                    Map<String, Object> record = objectMapper.convertValue(jsonNode, Map.class);
                    MediaCatalog mediaCatalog = parseMediaCatalogFromJSONRecord(record, recordNumber);
                    if (mediaCatalog != null) {
                        successList.add(mediaCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Record %d: %s", recordNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing JSON file: " + e.getMessage());
        }
    }

    private void processUploadCatalogCSVEnhanced(MultipartFile file, List<UploadCatalog> successList, BulkUploadResult result) {
        try (InputStreamReader isr = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(isr)) {
            
            String[] headers = csvReader.readNext();
            if (headers == null) {
                result.addError("CSV file is empty");
                return;
            }
            
            String[] row;
            int rowNumber = 1;
            
            while ((row = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    UploadCatalog uploadCatalog = parseUploadCatalogFromCSVRow(row, rowNumber);
                    if (uploadCatalog != null) {
                        successList.add(uploadCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", rowNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing CSV file: " + e.getMessage());
        }
    }

    private void processUploadCatalogExcelEnhanced(MultipartFile file, List<UploadCatalog> successList, BulkUploadResult result) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 0;
            
            for (Row row : sheet) {
                rowNumber++;
                if (rowNumber == 1) continue; // Skip header row
                
                try {
                    UploadCatalog uploadCatalog = parseUploadCatalogFromExcelRow(row, rowNumber);
                    if (uploadCatalog != null) {
                        successList.add(uploadCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", rowNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing Excel file: " + e.getMessage());
        }
    }

    private void processUploadCatalogJSONEnhanced(MultipartFile file, List<UploadCatalog> successList, BulkUploadResult result) {
        try (InputStreamReader isr = new InputStreamReader(file.getInputStream())) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonArray = objectMapper.readTree(isr);
            
            if (!jsonArray.isArray()) {
                result.addError("JSON file must contain an array of objects");
                return;
            }
            
            int recordNumber = 0;
            for (JsonNode jsonNode : jsonArray) {
                recordNumber++;
                try {
                    Map<String, Object> record = objectMapper.convertValue(jsonNode, Map.class);
                    UploadCatalog uploadCatalog = parseUploadCatalogFromJSONRecord(record, recordNumber);
                    if (uploadCatalog != null) {
                        successList.add(uploadCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Record %d: %s", recordNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing JSON file: " + e.getMessage());
        }
    }

    private void processStatesCatalogCSVEnhanced(MultipartFile file, List<StatesCatalog> successList, BulkUploadResult result) {
        try (InputStreamReader isr = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(isr)) {
            
            String[] headers = csvReader.readNext();
            if (headers == null) {
                result.addError("CSV file is empty");
                return;
            }
            
            String[] row;
            int rowNumber = 1;
            
            while ((row = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    StatesCatalog statesCatalog = parseStatesCatalogFromCSVRow(row, rowNumber);
                    if (statesCatalog != null) {
                        successList.add(statesCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", rowNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing CSV file: " + e.getMessage());
        }
    }

    private void processStatesCatalogExcelEnhanced(MultipartFile file, List<StatesCatalog> successList, BulkUploadResult result) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 0;
            
            for (Row row : sheet) {
                rowNumber++;
                if (rowNumber == 1) continue; // Skip header row
                
                try {
                    StatesCatalog statesCatalog = parseStatesCatalogFromExcelRow(row, rowNumber);
                    if (statesCatalog != null) {
                        successList.add(statesCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Row %d: %s", rowNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing Excel file: " + e.getMessage());
        }
    }

    private void processStatesCatalogJSONEnhanced(MultipartFile file, List<StatesCatalog> successList, BulkUploadResult result) {
        try (InputStreamReader isr = new InputStreamReader(file.getInputStream())) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonArray = objectMapper.readTree(isr);
            
            if (!jsonArray.isArray()) {
                result.addError("JSON file must contain an array of objects");
                return;
            }
            
            int recordNumber = 0;
            for (JsonNode jsonNode : jsonArray) {
                recordNumber++;
                try {
                    Map<String, Object> record = objectMapper.convertValue(jsonNode, Map.class);
                    StatesCatalog statesCatalog = parseStatesCatalogFromJSONRecord(record, recordNumber);
                    if (statesCatalog != null) {
                        successList.add(statesCatalog);
                    }
                } catch (Exception e) {
                    result.addError(String.format("Record %d: %s", recordNumber, e.getMessage()));
                }
            }
        } catch (Exception e) {
            result.addError("Error processing JSON file: " + e.getMessage());
        }
    }

    private void saveContentCatalogEntries(List<ContentCatalog> successList, BulkUploadResult result) {
        int savedCount = 0;
        for (ContentCatalog contentCatalog : successList) {
            try {
                ContentCatalog saved = contentCatalogRepository.save(contentCatalog);
                // Auto-create linked upload catalog
                createLinkedUploadCatalog(saved, result);
                savedCount++;
            } catch (Exception e) {
                String errorMsg = String.format("Error saving content catalog '%s': %s", 
                                                contentCatalog.getMediaCatalogName(), e.getMessage());
                result.addError(errorMsg);
                logger.error("Failed to save content catalog entry: {}", contentCatalog.getMediaCatalogName(), e);
            }
        }
        result.getAdditionalData().put("savedEntries", savedCount);
        result.getAdditionalData().put("autoCreatedUploadEntries", savedCount); // Assuming each creates one
    }

    private void saveMediaCatalogEntries(List<MediaCatalog> successList, BulkUploadResult result) {
        int savedCount = 0;
        int updatedCount = 0;
        
        for (MediaCatalog mediaCatalog : successList) {
            try {
                // Check for duplicates before saving
                Optional<MediaCatalog> existingMedia = mediaCatalogRepository.findByNameAndLanguageNullSafe(
                    mediaCatalog.getName(), mediaCatalog.getLanguage());
                
                if (existingMedia.isPresent()) {
                    // Update existing record
                    MediaCatalog existing = existingMedia.get();
                    existing.setType(mediaCatalog.getType());
                    existing.setDownloadStatus(mediaCatalog.getDownloadStatus());
                    existing.setPlatform(mediaCatalog.getPlatform());
                    existing.setLocation(mediaCatalog.getLocation());
                    existing.setDescription(mediaCatalog.getDescription());
                    existing.setFunFacts(mediaCatalog.getFunFacts());
                    existing.setLanguage(mediaCatalog.getLanguage());
                    existing.setMainGenre(mediaCatalog.getMainGenre());
                    existing.setSubGenres(mediaCatalog.getSubGenres());
                    existing.setUpdatedBy("enhanced-bulk-upload");
                    
                    mediaCatalogRepository.save(existing);
                    updatedCount++;
                    logger.debug("Updated existing media catalog: {} ({})", mediaCatalog.getName(), mediaCatalog.getLanguage());
                } else {
                    // Create new record
                    mediaCatalog.setCreatedBy("enhanced-bulk-upload");
                    mediaCatalog.setUpdatedBy("enhanced-bulk-upload");
                    mediaCatalogRepository.save(mediaCatalog);
                    savedCount++;
                    logger.debug("Created new media catalog: {} ({})", mediaCatalog.getName(), mediaCatalog.getLanguage());
                }
            } catch (Exception e) {
                String errorMsg = String.format("Error processing media catalog '%s' (%s): %s", 
                                                mediaCatalog.getName(), mediaCatalog.getLanguage(), e.getMessage());
                result.addError(errorMsg);
                logger.error("Failed to process media catalog entry: {} ({})", 
                           mediaCatalog.getName(), mediaCatalog.getLanguage(), e);
            }
        }
        
        result.getAdditionalData().put("newEntries", savedCount);
        result.getAdditionalData().put("updatedEntries", updatedCount);
        
        if (updatedCount > 0) {
            result.addWarning(String.format("%d existing entries were updated instead of creating duplicates.", updatedCount));
        }
    }

    private void saveUploadCatalogEntries(List<UploadCatalog> successList, BulkUploadResult result) {
        int savedCount = 0;
        int linkedCount = 0;
        
        for (UploadCatalog uploadCatalog : successList) {
            try {
                // First, check if content catalog entry exists with this link
                ContentCatalog contentCatalog = findOrCreateContentCatalogEntry(uploadCatalog, result);
                
                // Save upload catalog with linked content catalog link
                uploadCatalog.setLinkedContentCatalogLink(contentCatalog.getLink());
                UploadCatalog savedUpload = uploadCatalogRepository.save(uploadCatalog);
                savedCount++;
                
                // Update content catalog with linked upload link if not already set
                if (contentCatalog.getLinkedUploadCatalogLink() == null) {
                    contentCatalog.setLinkedUploadCatalogLink(savedUpload.getContentCatalogLink());
                    contentCatalogRepository.save(contentCatalog);
                    linkedCount++;
                }
            } catch (Exception e) {
                String errorMsg = String.format("Error saving upload catalog '%s': %s", 
                                                uploadCatalog.getMediaCatalogName(), e.getMessage());
                result.addError(errorMsg);
                logger.error("Failed to save upload catalog entry: {}", uploadCatalog.getMediaCatalogName(), e);
            }
        }
        
        result.getAdditionalData().put("savedEntries", savedCount);
        result.getAdditionalData().put("linkedContentEntries", linkedCount);
    }

    private void saveStatesCatalogEntries(List<StatesCatalog> successList, BulkUploadResult result) {
        int savedCount = 0;
        
        for (StatesCatalog statesCatalog : successList) {
            try {
                statesCatalogRepository.save(statesCatalog);
                savedCount++;
            } catch (Exception e) {
                String errorMsg = String.format("Error saving states catalog record: %s", e.getMessage());
                result.addError(errorMsg);
                logger.error("Failed to save states catalog record", e);
            }
        }
        
        result.getAdditionalData().put("savedEntries", savedCount);
    }

    private void createLinkedUploadCatalog(ContentCatalog contentCatalog, BulkUploadResult result) {
        try {
            UploadCatalog linkedUploadCatalog = new UploadCatalog();
            linkedUploadCatalog.setContentCatalogLink(contentCatalog.getLink());
            linkedUploadCatalog.setContentBlock(contentCatalog.getLink().hashCode() + ""); // Use link hash as content block
            linkedUploadCatalog.setMediaCatalogType(mapContentMediaTypeToUploadMediaType(contentCatalog.getMediaCatalogType()));
            linkedUploadCatalog.setMediaCatalogName(contentCatalog.getMediaCatalogName());
            linkedUploadCatalog.setContentCatalogLocation(contentCatalog.getLocation());
            linkedUploadCatalog.setUploadCatalogLocation("");
            linkedUploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.NEW);
            linkedUploadCatalog.setUploadCatalogCaption("Auto-created from enhanced bulk Content Catalog import: " + contentCatalog.getMediaCatalogName());
            linkedUploadCatalog.setLinkedContentCatalogLink(contentCatalog.getLink());
            
            UploadCatalog savedUpload = uploadCatalogRepository.save(linkedUploadCatalog);
            
            // Update content catalog with link
            contentCatalog.setLinkedUploadCatalogLink(savedUpload.getContentCatalogLink());
            contentCatalogRepository.save(contentCatalog);
            
            logger.debug("Created linked upload catalog for content: {}", contentCatalog.getMediaCatalogName());
            
        } catch (Exception e) {
            String warningMsg = String.format("Warning: Could not create linked upload catalog for '%s': %s", 
                                              contentCatalog.getMediaCatalogName(), e.getMessage());
            result.addWarning(warningMsg);
            logger.warn("Error creating linked upload catalog for: {} - {}", 
                       contentCatalog.getMediaCatalogName(), e.getMessage(), e);
        }
    }

    private ContentCatalog findOrCreateContentCatalogEntry(UploadCatalog uploadCatalog, BulkUploadResult result) {
        try {
            // First, try to find existing content catalog entry with the same link
            Optional<ContentCatalog> existingContent = contentCatalogRepository.findByLink(uploadCatalog.getContentCatalogLink());
            
            if (existingContent.isPresent()) {
                ContentCatalog existing = existingContent.get();
                
                // Update the existing entry with new media catalog name if it doesn't contain the upload's name
                String existingNames = existing.getMediaCatalogName();
                String newName = uploadCatalog.getMediaCatalogName();
                
                if (!existing.hasMediaCatalogName(newName)) {
                    existing.addMediaCatalogName(newName);
                    existing.setUpdatedBy("enhanced-upload-catalog-integration");
                    return contentCatalogRepository.save(existing);
                }
                
                return existing;
            } else {
                // Create new content catalog entry
                ContentCatalog newContentCatalog = new ContentCatalog();
                newContentCatalog.setLink(uploadCatalog.getContentCatalogLink());
                newContentCatalog.setMediaCatalogType(mapUploadMediaTypeToContentMediaType(uploadCatalog.getMediaCatalogType()));
                newContentCatalog.setMediaCatalogName(uploadCatalog.getMediaCatalogName());
                newContentCatalog.setStatus(ContentCatalog.ContentStatus.NEW);
                newContentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
                newContentCatalog.setUploadContentStatus(ContentCatalog.UploadContentStatus.PENDING_UPLOAD);
                newContentCatalog.setLocation(uploadCatalog.getContentCatalogLocation());
                newContentCatalog.setCreatedBy("enhanced-upload-catalog-integration");
                newContentCatalog.setUpdatedBy("enhanced-upload-catalog-integration");
                
                return contentCatalogRepository.save(newContentCatalog);
            }
        } catch (Exception e) {
            String errorMsg = String.format("Error finding or creating content catalog for link '%s': %s", 
                                           uploadCatalog.getContentCatalogLink(), e.getMessage());
            result.addError(errorMsg);
            logger.error("Error in findOrCreateContentCatalogEntry", e);
            
            // Return a basic content catalog as fallback
            ContentCatalog fallback = new ContentCatalog();
            fallback.setLink(uploadCatalog.getContentCatalogLink());
            fallback.setMediaCatalogType(mapUploadMediaTypeToContentMediaType(uploadCatalog.getMediaCatalogType()));
            fallback.setMediaCatalogName(uploadCatalog.getMediaCatalogName());
            fallback.setStatus(ContentCatalog.ContentStatus.NEW);
            fallback.setPriority(ContentCatalog.Priority.MEDIUM);
            return fallback;
        }
    }

    private UploadCatalog.MediaType mapContentMediaTypeToUploadMediaType(ContentCatalog.MediaType contentMediaType) {
        switch (contentMediaType) {
            case MOVIE: return UploadCatalog.MediaType.MOVIE;
            case ALBUM: return UploadCatalog.MediaType.ALBUM;
            case WEB_SERIES: return UploadCatalog.MediaType.WEB_SERIES;
            case DOCUMENTARY: return UploadCatalog.MediaType.DOCUMENTARY;
            default: return UploadCatalog.MediaType.MOVIE;
        }
    }

    // Enhanced parsing methods with better error handling
    private ContentCatalog parseContentCatalogFromCSVRecord(String[] record, int rowNumber) {
        if (record.length < 4) {
            throw new IllegalArgumentException(String.format("Insufficient columns in row %d. Expected at least 4 columns, found %d.", rowNumber, record.length));
        }
        
        try {
            ContentCatalog contentCatalog = new ContentCatalog();
            contentCatalog.setLink(cleanString(record[0]));
            contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(cleanString(record[1]).toUpperCase().replace("-", "_")));
            contentCatalog.setMediaCatalogName(cleanString(record[2]));
            contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(cleanString(record[3]).toUpperCase().replace("-", "_")));
            
            // Optional fields with validation
            if (record.length > 4 && !cleanString(record[4]).isEmpty()) {
                contentCatalog.setPriority(ContentCatalog.Priority.valueOf(cleanString(record[4]).toUpperCase()));
            } else {
                contentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
            }
            
            // Add other optional fields...
            
            return contentCatalog;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid data in row %d: %s", rowNumber, e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unexpected error parsing row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private ContentCatalog parseContentCatalogFromExcelRow(Row row, int rowNumber) {
        try {
            ContentCatalog contentCatalog = new ContentCatalog();
            
            Cell linkCell = row.getCell(0);
            Cell typeCell = row.getCell(1);
            Cell nameCell = row.getCell(2);
            Cell statusCell = row.getCell(3);
            
            if (linkCell == null || typeCell == null || nameCell == null || statusCell == null) {
                throw new IllegalArgumentException("Missing required columns in row " + rowNumber);
            }
            
            contentCatalog.setLink(cleanString(linkCell.getStringCellValue()));
            contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(cleanString(typeCell.getStringCellValue()).toUpperCase().replace("-", "_")));
            contentCatalog.setMediaCatalogName(cleanString(nameCell.getStringCellValue()));
            contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(cleanString(statusCell.getStringCellValue()).toUpperCase().replace("-", "_")));
            
            // Optional priority field
            Cell priorityCell = row.getCell(4);
            if (priorityCell != null && !cleanString(priorityCell.getStringCellValue()).isEmpty()) {
                contentCatalog.setPriority(ContentCatalog.Priority.valueOf(cleanString(priorityCell.getStringCellValue()).toUpperCase()));
            } else {
                contentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
            }
            
            return contentCatalog;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid data in row %d: %s", rowNumber, e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unexpected error parsing row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private ContentCatalog parseContentCatalogFromJSONRecord(Map<String, Object> record, int recordNumber) {
        try {
            ContentCatalog contentCatalog = new ContentCatalog();
            
            Object link = record.get("link");
            Object type = record.get("type");
            Object name = record.get("name");
            Object status = record.get("status");
            
            if (link == null || type == null || name == null || status == null) {
                throw new IllegalArgumentException("Missing required fields in record " + recordNumber);
            }
            
            contentCatalog.setLink(cleanString(link.toString()));
            contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(cleanString(type.toString()).toUpperCase().replace("-", "_")));
            contentCatalog.setMediaCatalogName(cleanString(name.toString()));
            contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(cleanString(status.toString()).toUpperCase().replace("-", "_")));
            
            // Optional priority field
            Object priority = record.get("priority");
            if (priority != null && !cleanString(priority.toString()).isEmpty()) {
                contentCatalog.setPriority(ContentCatalog.Priority.valueOf(cleanString(priority.toString()).toUpperCase()));
            } else {
                contentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
            }
            
            return contentCatalog;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid data in record %d: %s", recordNumber, e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unexpected error parsing record %d: %s", recordNumber, e.getMessage()));
        }
    }

    // MediaCatalog parsing methods
    private MediaCatalog parseMediaCatalogFromCSVRow(String[] record, int rowNumber) {
        if (record.length < 3) {
            throw new IllegalArgumentException(String.format("Insufficient columns in row %d. Expected at least 3 columns, found %d.", rowNumber, record.length));
        }
        
        try {
            MediaCatalog mediaCatalog = new MediaCatalog();
            mediaCatalog.setName(cleanString(record[0]));
            mediaCatalog.setType(MediaCatalog.MediaType.valueOf(cleanString(record[1]).toUpperCase()));
            mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(cleanString(record[2]).toUpperCase()));
            
            // Optional fields
            if (record.length > 3 && !cleanString(record[3]).isEmpty()) {
                mediaCatalog.setPlatform(cleanString(record[3]));
            }
            if (record.length > 4 && !cleanString(record[4]).isEmpty()) {
                mediaCatalog.setLanguage(cleanString(record[4]));
            }
            
            return mediaCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing MediaCatalog row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private MediaCatalog parseMediaCatalogFromExcelRow(Row row, int rowNumber) {
        try {
            MediaCatalog mediaCatalog = new MediaCatalog();
            
            Cell nameCell = row.getCell(0);
            Cell typeCell = row.getCell(1);
            Cell statusCell = row.getCell(2);
            
            if (nameCell == null || typeCell == null || statusCell == null) {
                throw new IllegalArgumentException("Missing required columns in row " + rowNumber);
            }
            
            mediaCatalog.setName(cleanString(nameCell.getStringCellValue()));
            mediaCatalog.setType(MediaCatalog.MediaType.valueOf(cleanString(typeCell.getStringCellValue()).toUpperCase()));
            mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(cleanString(statusCell.getStringCellValue()).toUpperCase()));
            
            // Optional fields
            Cell platformCell = row.getCell(3);
            if (platformCell != null) {
                mediaCatalog.setPlatform(cleanString(platformCell.getStringCellValue()));
            }
            Cell languageCell = row.getCell(4);
            if (languageCell != null) {
                mediaCatalog.setLanguage(cleanString(languageCell.getStringCellValue()));
            }
            
            return mediaCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing MediaCatalog row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private MediaCatalog parseMediaCatalogFromJSONRecord(Map<String, Object> record, int recordNumber) {
        try {
            MediaCatalog mediaCatalog = new MediaCatalog();
            
            Object name = record.get("name");
            Object type = record.get("type");
            Object status = record.get("status");
            
            if (name == null || type == null || status == null) {
                throw new IllegalArgumentException("Missing required fields in record " + recordNumber);
            }
            
            mediaCatalog.setName(cleanString(name.toString()));
            mediaCatalog.setType(MediaCatalog.MediaType.valueOf(cleanString(type.toString()).toUpperCase()));
            mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(cleanString(status.toString()).toUpperCase()));
            
            // Optional fields
            Object platform = record.get("platform");
            if (platform != null) {
                mediaCatalog.setPlatform(cleanString(platform.toString()));
            }
            Object language = record.get("language");
            if (language != null) {
                mediaCatalog.setLanguage(cleanString(language.toString()));
            }
            
            return mediaCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing MediaCatalog record %d: %s", recordNumber, e.getMessage()));
        }
    }

    // UploadCatalog parsing methods
    private UploadCatalog parseUploadCatalogFromCSVRow(String[] record, int rowNumber) {
        if (record.length < 2) {
            throw new IllegalArgumentException(String.format("Insufficient columns in row %d. Expected at least 2 columns, found %d.", rowNumber, record.length));
        }
        
        try {
            UploadCatalog uploadCatalog = new UploadCatalog();
            uploadCatalog.setContentCatalogLink(cleanString(record[0]));
            uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(cleanString(record[1]).toUpperCase()));
            
            // Optional fields
            if (record.length > 2 && !cleanString(record[2]).isEmpty()) {
                uploadCatalog.setLinkedContentCatalogLink(cleanString(record[2]));
            }
            
            return uploadCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing UploadCatalog row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private UploadCatalog parseUploadCatalogFromExcelRow(Row row, int rowNumber) {
        try {
            UploadCatalog uploadCatalog = new UploadCatalog();
            
            Cell linkCell = row.getCell(0);
            Cell statusCell = row.getCell(1);
            
            if (linkCell == null || statusCell == null) {
                throw new IllegalArgumentException("Missing required columns in row " + rowNumber);
            }
            
            uploadCatalog.setContentCatalogLink(cleanString(linkCell.getStringCellValue()));
            uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(cleanString(statusCell.getStringCellValue()).toUpperCase()));
            
            // Optional fields
            Cell linkedCell = row.getCell(2);
            if (linkedCell != null) {
                uploadCatalog.setLinkedContentCatalogLink(cleanString(linkedCell.getStringCellValue()));
            }
            
            return uploadCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing UploadCatalog row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private UploadCatalog parseUploadCatalogFromJSONRecord(Map<String, Object> record, int recordNumber) {
        try {
            UploadCatalog uploadCatalog = new UploadCatalog();
            
            Object link = record.get("link");
            Object status = record.get("status");
            
            if (link == null || status == null) {
                throw new IllegalArgumentException("Missing required fields in record " + recordNumber);
            }
            
            uploadCatalog.setContentCatalogLink(cleanString(link.toString()));
            uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(cleanString(status.toString()).toUpperCase()));
            
            // Optional fields
            Object linkedLink = record.get("linkedContentCatalogLink");
            if (linkedLink != null) {
                uploadCatalog.setLinkedContentCatalogLink(cleanString(linkedLink.toString()));
            }
            
            return uploadCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing UploadCatalog record %d: %s", recordNumber, e.getMessage()));
        }
    }

    // StatesCatalog parsing methods
    private StatesCatalog parseStatesCatalogFromCSVRow(String[] record, int rowNumber) {
        if (record.length < 2) {
            throw new IllegalArgumentException(String.format("Insufficient columns in row %d. Expected at least 2 columns, found %d.", rowNumber, record.length));
        }
        
        try {
            StatesCatalog statesCatalog = new StatesCatalog();
            statesCatalog.setStateName(cleanString(record[0]));
            statesCatalog.setStatus(StatesCatalog.Status.valueOf(cleanString(record[1]).toUpperCase()));
            
            return statesCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing StatesCatalog row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private StatesCatalog parseStatesCatalogFromExcelRow(Row row, int rowNumber) {
        try {
            StatesCatalog statesCatalog = new StatesCatalog();
            
            Cell nameCell = row.getCell(0);
            Cell statusCell = row.getCell(1);
            
            if (nameCell == null || statusCell == null) {
                throw new IllegalArgumentException("Missing required columns in row " + rowNumber);
            }
            
            statesCatalog.setStateName(cleanString(nameCell.getStringCellValue()));
            statesCatalog.setStatus(StatesCatalog.Status.valueOf(cleanString(statusCell.getStringCellValue()).toUpperCase()));
            
            return statesCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing StatesCatalog row %d: %s", rowNumber, e.getMessage()));
        }
    }

    private StatesCatalog parseStatesCatalogFromJSONRecord(Map<String, Object> record, int recordNumber) {
        try {
            StatesCatalog statesCatalog = new StatesCatalog();
            
            Object name = record.get("name");
            Object status = record.get("status");
            
            if (name == null || status == null) {
                throw new IllegalArgumentException("Missing required fields in record " + recordNumber);
            }
            
            statesCatalog.setStateName(cleanString(name.toString()));
            statesCatalog.setStatus(StatesCatalog.Status.valueOf(cleanString(status.toString()).toUpperCase()));
            
            return statesCatalog;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing StatesCatalog record %d: %s", recordNumber, e.getMessage()));
        }
    }

    /**
     * Maps upload catalog media type to content catalog media type
     */
    private ContentCatalog.MediaType mapUploadMediaTypeToContentMediaType(UploadCatalog.MediaType uploadMediaType) {
        switch (uploadMediaType) {
            case MOVIE:
                return ContentCatalog.MediaType.MOVIE;
            case ALBUM:
                return ContentCatalog.MediaType.ALBUM;
            case WEB_SERIES:
                return ContentCatalog.MediaType.WEB_SERIES;
            case DOCUMENTARY:
                return ContentCatalog.MediaType.DOCUMENTARY;
            default:
                return ContentCatalog.MediaType.MOVIE;
        }
    }

    private String cleanString(String input) {
        if (input == null) return "";
        return input.replaceAll("\"", "").trim();
    }
}