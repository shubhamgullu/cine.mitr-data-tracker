package com.cinemitr.service;

import com.cinemitr.model.ContentCatalog;
import com.cinemitr.model.MediaCatalog;
import com.cinemitr.model.UploadCatalog;
import com.cinemitr.repository.ContentCatalogRepository;
import com.cinemitr.repository.MediaCatalogRepository;
import com.cinemitr.repository.UploadCatalogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class BulkUploadService {

    @Autowired
    private ContentCatalogRepository contentCatalogRepository;

    @Autowired
    private UploadCatalogRepository uploadCatalogRepository;

    @Autowired
    private MediaCatalogRepository mediaCatalogRepository;

    public Map<String, Object> processContentCatalogBulkUpload(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<ContentCatalog> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        
        String fileName = file.getOriginalFilename();
        
        try {
            if (fileName.endsWith(".csv")) {
                processContentCatalogCSV(file, successList, errorList);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processContentCatalogExcel(file, successList, errorList);
            } else if (fileName.endsWith(".json")) {
                processContentCatalogJSON(file, successList, errorList);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Please use CSV, Excel, or JSON files.");
            }
            
            // Save successful entries
            for (ContentCatalog contentCatalog : successList) {
                try {
                    ContentCatalog saved = contentCatalogRepository.save(contentCatalog);
                    // Auto-create linked upload catalog
                    createLinkedUploadCatalog(saved);
                } catch (Exception e) {
                    errorList.add("Error saving content catalog: " + contentCatalog.getMediaCatalogName() + " - " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            errorList.add("File processing error: " + e.getMessage());
        }
        
        result.put("successCount", successList.size());
        result.put("errorCount", errorList.size());
        result.put("errors", errorList);
        return result;
    }

    public Map<String, Object> processMediaCatalogBulkUpload(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<MediaCatalog> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        
        String fileName = file.getOriginalFilename();
        
        try {
            if (fileName.endsWith(".csv")) {
                processMediaCatalogCSV(file, successList, errorList);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processMediaCatalogExcel(file, successList, errorList);
            } else if (fileName.endsWith(".json")) {
                processMediaCatalogJSON(file, successList, errorList);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Please use CSV, Excel, or JSON files.");
            }
            
            // Save successful entries
            for (MediaCatalog mediaCatalog : successList) {
                try {
                    mediaCatalogRepository.save(mediaCatalog);
                } catch (Exception e) {
                    errorList.add("Error saving media catalog: " + mediaCatalog.getName() + " - " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            errorList.add("File processing error: " + e.getMessage());
        }
        
        result.put("successCount", successList.size());
        result.put("errorCount", errorList.size());
        result.put("errors", errorList);
        return result;
    }

    public Map<String, Object> processUploadCatalogBulkUpload(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<UploadCatalog> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        
        String fileName = file.getOriginalFilename();
        
        try {
            if (fileName.endsWith(".csv")) {
                processUploadCatalogCSV(file, successList, errorList);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                processUploadCatalogExcel(file, successList, errorList);
            } else if (fileName.endsWith(".json")) {
                processUploadCatalogJSON(file, successList, errorList);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Please use CSV, Excel, or JSON files.");
            }
            
            // Save successful entries
            for (UploadCatalog uploadCatalog : successList) {
                try {
                    uploadCatalogRepository.save(uploadCatalog);
                } catch (Exception e) {
                    errorList.add("Error saving upload catalog: " + uploadCatalog.getMediaCatalogName() + " - " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            errorList.add("File processing error: " + e.getMessage());
        }
        
        result.put("successCount", successList.size());
        result.put("errorCount", errorList.size());
        result.put("errors", errorList);
        return result;
    }

    private void processContentCatalogCSV(MultipartFile file, List<ContentCatalog> successList, List<String> errorList) throws IOException {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                errorList.add("CSV file is empty");
                return;
            }
            
            // Skip header row
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                try {
                    ContentCatalog contentCatalog = parseContentCatalogFromCSVRecord(record);
                    successList.add(contentCatalog);
                } catch (Exception e) {
                    errorList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (CsvException e) {
            errorList.add("CSV parsing error: " + e.getMessage());
        }
    }

    private void processUploadCatalogCSV(MultipartFile file, List<UploadCatalog> successList, List<String> errorList) throws IOException {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                errorList.add("CSV file is empty");
                return;
            }
            
            // Skip header row
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                try {
                    UploadCatalog uploadCatalog = parseUploadCatalogFromCSVRecord(record);
                    successList.add(uploadCatalog);
                } catch (Exception e) {
                    errorList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (CsvException e) {
            errorList.add("CSV parsing error: " + e.getMessage());
        }
    }

    private void processContentCatalogJSON(MultipartFile file, List<ContentCatalog> successList, List<String> errorList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> records = mapper.readValue(file.getInputStream(), new TypeReference<List<Map<String, Object>>>(){});
            
            for (int i = 0; i < records.size(); i++) {
                try {
                    ContentCatalog contentCatalog = parseContentCatalogFromJSONRecord(records.get(i));
                    successList.add(contentCatalog);
                } catch (Exception e) {
                    errorList.add("Record " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errorList.add("JSON parsing error: " + e.getMessage());
        }
    }

    private void processUploadCatalogJSON(MultipartFile file, List<UploadCatalog> successList, List<String> errorList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> records = mapper.readValue(file.getInputStream(), new TypeReference<List<Map<String, Object>>>(){});
            
            for (int i = 0; i < records.size(); i++) {
                try {
                    UploadCatalog uploadCatalog = parseUploadCatalogFromJSONRecord(records.get(i));
                    successList.add(uploadCatalog);
                } catch (Exception e) {
                    errorList.add("Record " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errorList.add("JSON parsing error: " + e.getMessage());
        }
    }

    private ContentCatalog parseContentCatalogFromCSVRecord(String[] record) {
        if (record.length < 4) {
            throw new IllegalArgumentException("Insufficient columns. Expected at least 4 columns.");
        }
        
        ContentCatalog contentCatalog = new ContentCatalog();
        contentCatalog.setLink(record[0].replaceAll("\"", ""));
        contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(record[1].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        contentCatalog.setMediaCatalogName(record[2].replaceAll("\"", ""));
        contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(record[3].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        
        // Optional fields
        if (record.length > 4 && !record[4].trim().isEmpty()) {
            contentCatalog.setPriority(ContentCatalog.Priority.valueOf(record[4].replaceAll("\"", "").toUpperCase()));
        } else {
            contentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
        }
        
        if (record.length > 5) contentCatalog.setLocation(record[5].replaceAll("\"", ""));
        if (record.length > 6) contentCatalog.setMetadata(record[6].replaceAll("\"", ""));
        if (record.length > 7 && !record[7].trim().isEmpty()) {
            contentCatalog.setLikeStates(ContentCatalog.LikeState.valueOf(record[7].replaceAll("\"", "").toUpperCase()));
        }
        if (record.length > 8) contentCatalog.setCommentStates(record[8].replaceAll("\"", ""));
        if (record.length > 9 && !record[9].trim().isEmpty()) {
            contentCatalog.setUploadContentStatus(ContentCatalog.UploadContentStatus.valueOf(record[9].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        }
        
        return contentCatalog;
    }

    private UploadCatalog parseUploadCatalogFromCSVRecord(String[] record) {
        if (record.length < 7) {
            throw new IllegalArgumentException("Insufficient columns. Expected at least 7 columns.");
        }
        
        UploadCatalog uploadCatalog = new UploadCatalog();
        uploadCatalog.setContentCatalogLink(record[0].replaceAll("\"", ""));
        uploadCatalog.setContentBlock(record[1].replaceAll("\"", ""));
        uploadCatalog.setMediaCatalogType(UploadCatalog.MediaType.valueOf(record[2].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        uploadCatalog.setMediaCatalogName(record[3].replaceAll("\"", ""));
        uploadCatalog.setContentCatalogLocation(record[4].replaceAll("\"", ""));
        uploadCatalog.setUploadCatalogLocation(record[5].replaceAll("\"", ""));
        uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(record[6].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        
        // Optional caption
        if (record.length > 7) {
            uploadCatalog.setUploadCatalogCaption(record[7].replaceAll("\"", ""));
        }
        
        return uploadCatalog;
    }

    private ContentCatalog parseContentCatalogFromJSONRecord(Map<String, Object> record) {
        ContentCatalog contentCatalog = new ContentCatalog();
        contentCatalog.setLink((String) record.get("link"));
        contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(((String) record.get("mediaCatalogType")).toUpperCase()));
        contentCatalog.setMediaCatalogName((String) record.get("mediaCatalogName"));
        contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(((String) record.get("status")).toUpperCase()));
        
        if (record.containsKey("priority")) {
            contentCatalog.setPriority(ContentCatalog.Priority.valueOf(((String) record.get("priority")).toUpperCase()));
        }
        if (record.containsKey("location")) {
            contentCatalog.setLocation((String) record.get("location"));
        }
        if (record.containsKey("metadata")) {
            contentCatalog.setMetadata((String) record.get("metadata"));
        }
        if (record.containsKey("likeStates")) {
            contentCatalog.setLikeStates(ContentCatalog.LikeState.valueOf(((String) record.get("likeStates")).toUpperCase()));
        }
        if (record.containsKey("commentStates")) {
            contentCatalog.setCommentStates((String) record.get("commentStates"));
        }
        if (record.containsKey("uploadContentStatus")) {
            contentCatalog.setUploadContentStatus(ContentCatalog.UploadContentStatus.valueOf(((String) record.get("uploadContentStatus")).toUpperCase().replace("-", "_")));
        }
        
        return contentCatalog;
    }

    private UploadCatalog parseUploadCatalogFromJSONRecord(Map<String, Object> record) {
        UploadCatalog uploadCatalog = new UploadCatalog();
        uploadCatalog.setContentCatalogLink((String) record.get("contentCatalogLink"));
        uploadCatalog.setContentBlock((String) record.get("contentBlock"));
        uploadCatalog.setMediaCatalogType(UploadCatalog.MediaType.valueOf(((String) record.get("mediaCatalogType")).toUpperCase()));
        uploadCatalog.setMediaCatalogName((String) record.get("mediaCatalogName"));
        uploadCatalog.setContentCatalogLocation((String) record.get("contentCatalogLocation"));
        uploadCatalog.setUploadCatalogLocation((String) record.get("uploadCatalogLocation"));
        uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(((String) record.get("uploadStatus")).toUpperCase().replace("-", "_")));
        
        if (record.containsKey("uploadCatalogCaption")) {
            uploadCatalog.setUploadCatalogCaption((String) record.get("uploadCatalogCaption"));
        }
        
        return uploadCatalog;
    }

    // Excel processing methods (simplified - similar logic to CSV)
    private void processContentCatalogExcel(MultipartFile file, List<ContentCatalog> successList, List<String> errorList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    ContentCatalog contentCatalog = parseContentCatalogFromExcelRow(row);
                    successList.add(contentCatalog);
                } catch (Exception e) {
                    errorList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
    }

    private void processUploadCatalogExcel(MultipartFile file, List<UploadCatalog> successList, List<String> errorList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    UploadCatalog uploadCatalog = parseUploadCatalogFromExcelRow(row);
                    successList.add(uploadCatalog);
                } catch (Exception e) {
                    errorList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
    }

    private ContentCatalog parseContentCatalogFromExcelRow(Row row) {
        ContentCatalog contentCatalog = new ContentCatalog();
        
        contentCatalog.setLink(getCellValueAsString(row.getCell(0)));
        contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(getCellValueAsString(row.getCell(1)).toUpperCase().replace("-", "_")));
        contentCatalog.setMediaCatalogName(getCellValueAsString(row.getCell(2)));
        contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(getCellValueAsString(row.getCell(3)).toUpperCase().replace("-", "_")));
        
        if (row.getCell(4) != null && !getCellValueAsString(row.getCell(4)).trim().isEmpty()) {
            contentCatalog.setPriority(ContentCatalog.Priority.valueOf(getCellValueAsString(row.getCell(4)).toUpperCase()));
        } else {
            contentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
        }
        
        if (row.getCell(5) != null) contentCatalog.setLocation(getCellValueAsString(row.getCell(5)));
        if (row.getCell(6) != null) contentCatalog.setMetadata(getCellValueAsString(row.getCell(6)));
        
        return contentCatalog;
    }

    private UploadCatalog parseUploadCatalogFromExcelRow(Row row) {
        UploadCatalog uploadCatalog = new UploadCatalog();
        
        uploadCatalog.setContentCatalogLink(getCellValueAsString(row.getCell(0)));
        uploadCatalog.setContentBlock(getCellValueAsString(row.getCell(1)));
        uploadCatalog.setMediaCatalogType(UploadCatalog.MediaType.valueOf(getCellValueAsString(row.getCell(2)).toUpperCase().replace("-", "_")));
        uploadCatalog.setMediaCatalogName(getCellValueAsString(row.getCell(3)));
        uploadCatalog.setContentCatalogLocation(getCellValueAsString(row.getCell(4)));
        uploadCatalog.setUploadCatalogLocation(getCellValueAsString(row.getCell(5)));
        uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(getCellValueAsString(row.getCell(6)).toUpperCase().replace("-", "_")));
        
        if (row.getCell(7) != null) {
            uploadCatalog.setUploadCatalogCaption(getCellValueAsString(row.getCell(7)));
        }
        
        return uploadCatalog;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private void createLinkedUploadCatalog(ContentCatalog contentCatalog) {
        try {
            UploadCatalog linkedUploadCatalog = new UploadCatalog();
            linkedUploadCatalog.setContentCatalogLink(contentCatalog.getLink());
            linkedUploadCatalog.setContentBlock(contentCatalog.getId().toString());
            linkedUploadCatalog.setMediaCatalogType(mapContentMediaTypeToUploadMediaType(contentCatalog.getMediaCatalogType()));
            linkedUploadCatalog.setMediaCatalogName(contentCatalog.getMediaCatalogName());
            linkedUploadCatalog.setContentCatalogLocation(contentCatalog.getLocation());
            linkedUploadCatalog.setUploadCatalogLocation("");
            linkedUploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.NEW);
            linkedUploadCatalog.setUploadCatalogCaption("Auto-created from bulk Content Catalog import: " + contentCatalog.getMediaCatalogName());
            linkedUploadCatalog.setLinkedContentCatalogId(contentCatalog.getId());
            
            UploadCatalog savedUpload = uploadCatalogRepository.save(linkedUploadCatalog);
            
            // Update content catalog with link
            contentCatalog.setLinkedUploadCatalogId(savedUpload.getId());
            contentCatalogRepository.save(contentCatalog);
            
        } catch (Exception e) {
            System.err.println("Error creating linked upload catalog for: " + contentCatalog.getMediaCatalogName() + " - " + e.getMessage());
        }
    }

    private UploadCatalog.MediaType mapContentMediaTypeToUploadMediaType(ContentCatalog.MediaType contentMediaType) {
        switch (contentMediaType) {
            case MOVIE:
                return UploadCatalog.MediaType.MOVIE;
            case ALBUM:
                return UploadCatalog.MediaType.ALBUM;
            case WEB_SERIES:
                return UploadCatalog.MediaType.WEB_SERIES;
            case DOCUMENTARY:
                return UploadCatalog.MediaType.DOCUMENTARY;
            default:
                return UploadCatalog.MediaType.MOVIE;
        }
    }

    // ===== MEDIA CATALOG PROCESSING METHODS =====
    
    private void processMediaCatalogCSV(MultipartFile file, List<MediaCatalog> successList, List<String> errorList) throws IOException {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                errorList.add("CSV file is empty");
                return;
            }
            
            // Skip header row
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                try {
                    MediaCatalog mediaCatalog = parseMediaCatalogFromCSVRecord(record);
                    successList.add(mediaCatalog);
                } catch (Exception e) {
                    errorList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (CsvException e) {
            errorList.add("CSV parsing error: " + e.getMessage());
        }
    }

    private void processMediaCatalogJSON(MultipartFile file, List<MediaCatalog> successList, List<String> errorList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> records = mapper.readValue(file.getInputStream(), new TypeReference<List<Map<String, Object>>>(){});
            
            for (int i = 0; i < records.size(); i++) {
                try {
                    MediaCatalog mediaCatalog = parseMediaCatalogFromJSONRecord(records.get(i));
                    successList.add(mediaCatalog);
                } catch (Exception e) {
                    errorList.add("Record " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errorList.add("JSON parsing error: " + e.getMessage());
        }
    }

    private void processMediaCatalogExcel(MultipartFile file, List<MediaCatalog> successList, List<String> errorList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    MediaCatalog mediaCatalog = parseMediaCatalogFromExcelRow(row);
                    successList.add(mediaCatalog);
                } catch (Exception e) {
                    errorList.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
    }

    private MediaCatalog parseMediaCatalogFromCSVRecord(String[] record) {
        if (record.length < 3) {
            throw new IllegalArgumentException("Insufficient columns. Expected at least 3 columns (name, type, downloadStatus).");
        }
        
        MediaCatalog mediaCatalog = new MediaCatalog();
        
        // Required fields
        String name = record[0].replaceAll("\"", "").trim();
        mediaCatalog.setName(formatProperCase(name));
        
        mediaCatalog.setType(MediaCatalog.MediaType.valueOf(record[1].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(record[2].replaceAll("\"", "").toUpperCase().replace("-", "_")));
        
        // Optional fields (matching MediaCatalog model fields)
        if (record.length > 3 && !record[3].trim().isEmpty()) {
            mediaCatalog.setDescription(record[3].replaceAll("\"", "").trim());
        }
        if (record.length > 4 && !record[4].trim().isEmpty()) {
            mediaCatalog.setPlatform(record[4].replaceAll("\"", "").trim());
        }
        if (record.length > 5 && !record[5].trim().isEmpty()) {
            mediaCatalog.setLocation(record[5].replaceAll("\"", "").trim());
        }
        if (record.length > 6 && !record[6].trim().isEmpty()) {
            mediaCatalog.setFunFacts(record[6].replaceAll("\"", "").trim());
        }
        
        return mediaCatalog;
    }

    private MediaCatalog parseMediaCatalogFromJSONRecord(Map<String, Object> record) {
        MediaCatalog mediaCatalog = new MediaCatalog();
        
        // Required fields
        if (!record.containsKey("name") || !record.containsKey("type") || !record.containsKey("downloadStatus")) {
            throw new IllegalArgumentException("Missing required fields: name, type, or downloadStatus");
        }
        
        String name = (String) record.get("name");
        mediaCatalog.setName(formatProperCase(name.trim()));
        
        mediaCatalog.setType(MediaCatalog.MediaType.valueOf(((String) record.get("type")).toUpperCase().replace("-", "_")));
        mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(((String) record.get("downloadStatus")).toUpperCase().replace("-", "_")));
        
        // Optional fields (matching MediaCatalog model fields)
        if (record.containsKey("description") && record.get("description") != null) {
            mediaCatalog.setDescription(((String) record.get("description")).trim());
        }
        if (record.containsKey("platform") && record.get("platform") != null) {
            mediaCatalog.setPlatform(((String) record.get("platform")).trim());
        }
        if (record.containsKey("location") && record.get("location") != null) {
            mediaCatalog.setLocation(((String) record.get("location")).trim());
        }
        if (record.containsKey("funFacts") && record.get("funFacts") != null) {
            mediaCatalog.setFunFacts(((String) record.get("funFacts")).trim());
        }
        
        return mediaCatalog;
    }

    private MediaCatalog parseMediaCatalogFromExcelRow(Row row) {
        MediaCatalog mediaCatalog = new MediaCatalog();
        
        // Required fields validation
        if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null) {
            throw new IllegalArgumentException("Missing required fields: name, type, or downloadStatus");
        }
        
        // Required fields
        String name = getCellValueAsString(row.getCell(0));
        mediaCatalog.setName(formatProperCase(name.trim()));
        
        mediaCatalog.setType(MediaCatalog.MediaType.valueOf(getCellValueAsString(row.getCell(1)).toUpperCase().replace("-", "_")));
        mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(getCellValueAsString(row.getCell(2)).toUpperCase().replace("-", "_")));
        
        // Optional fields (matching MediaCatalog model fields)
        if (row.getCell(3) != null) {
            String description = getCellValueAsString(row.getCell(3)).trim();
            if (!description.isEmpty()) {
                mediaCatalog.setDescription(description);
            }
        }
        if (row.getCell(4) != null) {
            String platform = getCellValueAsString(row.getCell(4)).trim();
            if (!platform.isEmpty()) {
                mediaCatalog.setPlatform(platform);
            }
        }
        if (row.getCell(5) != null) {
            String location = getCellValueAsString(row.getCell(5)).trim();
            if (!location.isEmpty()) {
                mediaCatalog.setLocation(location);
            }
        }
        if (row.getCell(6) != null) {
            String funFacts = getCellValueAsString(row.getCell(6)).trim();
            if (!funFacts.isEmpty()) {
                mediaCatalog.setFunFacts(funFacts);
            }
        }
        
        return mediaCatalog;
    }

    /**
     * Formats text to proper case - capitalizes the first letter of each word
     * Example: "the avengers endgame" -> "The Avengers Endgame"
     */
    private String formatProperCase(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        String[] words = text.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            if (word.length() > 0) {
                // Capitalize first letter
                word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            }
            result.append(word);
            if (i < words.length - 1) {
                result.append(" ");
            }
        }
        
        return result.toString();
    }
}