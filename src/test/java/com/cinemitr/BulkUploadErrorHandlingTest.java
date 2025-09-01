package com.cinemitr;

import com.cinemitr.model.ContentCatalog;
import com.cinemitr.repository.ContentCatalogRepository;
import com.cinemitr.service.BulkUploadValidationService;
import com.cinemitr.service.EnhancedBulkUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BulkUploadErrorHandlingTest {

    @Autowired
    private BulkUploadValidationService validationService;

    @Autowired
    private EnhancedBulkUploadService enhancedBulkUploadService;

    @MockBean
    private ContentCatalogRepository contentCatalogRepository;

    @Test
    public void testDuplicateLinkValidation() {
        // Test data with duplicate links
        ContentCatalog catalog1 = new ContentCatalog();
        catalog1.setLink("https://example.com/movie1");
        catalog1.setMediaCatalogType(ContentCatalog.MediaType.MOVIE);
        catalog1.setMediaCatalogName("Test Movie");
        catalog1.setStatus(ContentCatalog.ContentStatus.NEW);

        ContentCatalog catalog2 = new ContentCatalog();
        catalog2.setLink("https://example.com/movie1"); // Duplicate link
        catalog2.setMediaCatalogType(ContentCatalog.MediaType.MOVIE);
        catalog2.setMediaCatalogName("Another Movie");
        catalog2.setStatus(ContentCatalog.ContentStatus.NEW);

        List<ContentCatalog> catalogs = Arrays.asList(catalog1, catalog2);

        // Mock repository to return false for existing links
        when(contentCatalogRepository.existsByLink(anyString())).thenReturn(false);

        // Validate
        BulkUploadValidationService.ValidationResult result = validationService.validateContentCatalogList(catalogs);

        // Assertions
        assertFalse(result.isValid(), "Validation should fail for duplicate links");
        assertTrue(result.getErrors().size() > 0, "Should have error messages");
        assertTrue(result.getDuplicateLinks().contains("https://example.com/movie1"), "Should identify the duplicate link");
    }

    @Test 
    public void testExistingLinkValidation() {
        // Test data with link that exists in database
        ContentCatalog catalog = new ContentCatalog();
        catalog.setLink("https://example.com/existing-movie");
        catalog.setMediaCatalogType(ContentCatalog.MediaType.MOVIE);
        catalog.setMediaCatalogName("Existing Movie");
        catalog.setStatus(ContentCatalog.ContentStatus.NEW);

        List<ContentCatalog> catalogs = Arrays.asList(catalog);

        // Mock repository to return true for this link (exists in database)
        when(contentCatalogRepository.existsByLink("https://example.com/existing-movie")).thenReturn(true);

        // Validate
        BulkUploadValidationService.ValidationResult result = validationService.validateContentCatalogList(catalogs);

        // Assertions
        assertFalse(result.isValid(), "Validation should fail for existing links");
        assertTrue(result.getErrors().size() > 0, "Should have error messages");
        assertTrue(result.getExistingLinks().contains("https://example.com/existing-movie"), "Should identify the existing link");
    }

    @Test
    public void testValidContentCatalogList() {
        // Test data with valid, unique links
        ContentCatalog catalog1 = new ContentCatalog();
        catalog1.setLink("https://example.com/movie1");
        catalog1.setMediaCatalogType(ContentCatalog.MediaType.MOVIE);
        catalog1.setMediaCatalogName("Test Movie 1");
        catalog1.setStatus(ContentCatalog.ContentStatus.NEW);

        ContentCatalog catalog2 = new ContentCatalog();
        catalog2.setLink("https://example.com/movie2");
        catalog2.setMediaCatalogType(ContentCatalog.MediaType.MOVIE);
        catalog2.setMediaCatalogName("Test Movie 2");
        catalog2.setStatus(ContentCatalog.ContentStatus.NEW);

        List<ContentCatalog> catalogs = Arrays.asList(catalog1, catalog2);

        // Mock repository to return false for all links (none exist)
        when(contentCatalogRepository.existsByLink(anyString())).thenReturn(false);

        // Validate
        BulkUploadValidationService.ValidationResult result = validationService.validateContentCatalogList(catalogs);

        // Assertions
        assertTrue(result.isValid(), "Validation should pass for valid data");
        assertEquals(0, result.getErrors().size(), "Should have no error messages");
        assertEquals(0, result.getDuplicateLinks().size(), "Should have no duplicates");
        assertEquals(0, result.getExistingLinks().size(), "Should have no existing links");
    }

    @Test
    public void testInvalidUrlFormat() {
        // Test data with invalid URL
        ContentCatalog catalog = new ContentCatalog();
        catalog.setLink("invalid-url-format");
        catalog.setMediaCatalogType(ContentCatalog.MediaType.MOVIE);
        catalog.setMediaCatalogName("Test Movie");
        catalog.setStatus(ContentCatalog.ContentStatus.NEW);

        List<ContentCatalog> catalogs = Arrays.asList(catalog);

        // Mock repository
        when(contentCatalogRepository.existsByLink(anyString())).thenReturn(false);

        // Validate
        BulkUploadValidationService.ValidationResult result = validationService.validateContentCatalogList(catalogs);

        // Assertions
        assertFalse(result.isValid(), "Validation should fail for invalid URL format");
        assertTrue(result.getErrors().size() > 0, "Should have error messages");
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("Invalid URL format")), 
                  "Should have URL format error");
    }

    @Test
    public void testMissingRequiredFields() {
        // Test data with missing required fields
        ContentCatalog catalog = new ContentCatalog();
        catalog.setLink("https://example.com/movie1");
        // Missing mediaType, mediaName, and status

        List<ContentCatalog> catalogs = Arrays.asList(catalog);

        // Mock repository
        when(contentCatalogRepository.existsByLink(anyString())).thenReturn(false);

        // Validate
        BulkUploadValidationService.ValidationResult result = validationService.validateContentCatalogList(catalogs);

        // Assertions
        assertFalse(result.isValid(), "Validation should fail for missing required fields");
        assertTrue(result.getErrors().size() >= 3, "Should have at least 3 error messages for missing fields");
    }

    @Test
    public void testEmptyFile() throws Exception {
        // Test empty file handling
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", 
            "empty.csv", 
            "text/csv", 
            new byte[0]
        );

        // Process the file
        Map<String, Object> result = enhancedBulkUploadService.processContentCatalogBulkUpload(emptyFile);

        // Assertions
        assertEquals(0, result.get("successCount"), "Success count should be 0 for empty file");
        assertTrue((Integer) result.get("errorCount") > 0, "Should have errors for empty file");
        
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        assertTrue(errors.stream().anyMatch(error -> error.toLowerCase().contains("empty")), 
                  "Should have empty file error");
    }

    @Test
    public void testInvalidFileFormat() throws Exception {
        // Test invalid file format
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file", 
            "invalid.txt", 
            "text/plain", 
            "Some text content".getBytes()
        );

        // Process the file
        Map<String, Object> result = enhancedBulkUploadService.processContentCatalogBulkUpload(invalidFile);

        // Assertions
        assertEquals(0, result.get("successCount"), "Success count should be 0 for invalid format");
        assertTrue((Integer) result.get("errorCount") > 0, "Should have errors for invalid format");
        
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        assertTrue(errors.stream().anyMatch(error -> error.toLowerCase().contains("format")), 
                  "Should have format error");
    }

    @Test
    public void testLargeCsvFile() throws Exception {
        // Create a CSV with duplicate links to test validation
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("link,mediaCatalogType,mediaCatalogName,status\n");
        csvContent.append("https://example.com/movie1,MOVIE,Movie 1,NEW\n");
        csvContent.append("https://example.com/movie1,MOVIE,Movie 1 Duplicate,NEW\n"); // Duplicate link
        csvContent.append("https://example.com/movie2,MOVIE,Movie 2,NEW\n");

        MockMultipartFile csvFile = new MockMultipartFile(
            "file", 
            "test.csv", 
            "text/csv", 
            csvContent.toString().getBytes()
        );

        // Mock repository
        when(contentCatalogRepository.existsByLink(anyString())).thenReturn(false);

        // Process the file
        Map<String, Object> result = enhancedBulkUploadService.processContentCatalogBulkUpload(csvFile);

        // Assertions
        assertTrue((Integer) result.get("errorCount") > 0, "Should have errors for duplicate links");
        
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        assertTrue(errors.stream().anyMatch(error -> error.toLowerCase().contains("duplicate")), 
                  "Should have duplicate link error");
    }
}