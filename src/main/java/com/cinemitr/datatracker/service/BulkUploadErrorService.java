package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.BulkUploadErrorDTO;
import com.cinemitr.datatracker.entity.BulkUploadError;
import com.cinemitr.datatracker.repository.BulkUploadErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BulkUploadErrorService {
    
    @Autowired
    private BulkUploadErrorRepository errorRepository;
    
    // Generate unique batch ID for each bulk upload session
    public String generateBatchId() {
        return "BATCH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Log a bulk upload error
    public BulkUploadErrorDTO logError(String uploadType, String batchId, Integer rowNumber, 
                                      String rawData, String errorType, String errorMessage,
                                      String fieldName, String attemptedValue, String suggestions) {
        BulkUploadError error = new BulkUploadError();
        error.setUploadType(uploadType.toUpperCase());
        error.setBatchId(batchId);
        error.setRowNumber(rowNumber);
        error.setRawData(rawData);
        error.setErrorType(errorType);
        error.setErrorMessage(errorMessage);
        error.setFieldName(fieldName);
        error.setAttemptedValue(attemptedValue);
        error.setSuggestions(suggestions);
        error.setIsResolved(false);
        
        BulkUploadError savedError = errorRepository.save(error);
        return convertToDTO(savedError);
    }
    
    // Quick method to log simple errors
    public BulkUploadErrorDTO logSimpleError(String uploadType, String batchId, Integer rowNumber,
                                            String errorMessage, String rawData) {
        return logError(uploadType, batchId, rowNumber, rawData, "PROCESSING_ERROR", 
                       errorMessage, null, null, null);
    }
    
    // Log validation error with specific field information
    public BulkUploadErrorDTO logValidationError(String uploadType, String batchId, Integer rowNumber,
                                                String rawData, String fieldName, String attemptedValue,
                                                String errorMessage, String suggestions) {
        return logError(uploadType, batchId, rowNumber, rawData, "VALIDATION_ERROR",
                       errorMessage, fieldName, attemptedValue, suggestions);
    }
    
    // Log duplicate error
    public BulkUploadErrorDTO logDuplicateError(String uploadType, String batchId, Integer rowNumber,
                                               String rawData, String fieldName, String attemptedValue) {
        String errorMessage = String.format("Duplicate entry found for field '%s' with value '%s'", 
                                           fieldName, attemptedValue);
        String suggestions = "Please check for existing records or modify the value to make it unique";
        return logError(uploadType, batchId, rowNumber, rawData, "DUPLICATE_ERROR",
                       errorMessage, fieldName, attemptedValue, suggestions);
    }
    
    // Log database constraint error
    public BulkUploadErrorDTO logConstraintError(String uploadType, String batchId, Integer rowNumber,
                                                String rawData, String errorMessage) {
        String suggestions = "Please check data format and constraints. Verify all required fields are present and valid.";
        return logError(uploadType, batchId, rowNumber, rawData, "CONSTRAINT_ERROR",
                       errorMessage, null, null, suggestions);
    }
    
    // Get all errors
    public List<BulkUploadErrorDTO> getAllErrors() {
        return errorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get errors by upload type
    public List<BulkUploadErrorDTO> getErrorsByUploadType(String uploadType) {
        return errorRepository.findByUploadTypeOrderByCreatedAtDesc(uploadType.toUpperCase())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get errors by batch ID
    public List<BulkUploadErrorDTO> getErrorsByBatchId(String batchId) {
        return errorRepository.findByBatchIdOrderByRowNumberAsc(batchId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get unresolved errors
    public List<BulkUploadErrorDTO> getUnresolvedErrors() {
        return errorRepository.findByIsResolvedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get unresolved errors by upload type
    public List<BulkUploadErrorDTO> getUnresolvedErrorsByUploadType(String uploadType) {
        return errorRepository.findByUploadTypeAndIsResolvedFalseOrderByCreatedAtDesc(uploadType.toUpperCase())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get recent errors (last 7 days)
    public List<BulkUploadErrorDTO> getRecentErrors() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date fromDate = cal.getTime();
        
        return errorRepository.findRecentErrors(fromDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Mark error as resolved
    public BulkUploadErrorDTO markAsResolved(Long errorId, String resolutionNotes) {
        BulkUploadError error = errorRepository.findById(errorId)
                .orElseThrow(() -> new RuntimeException("Error not found with id: " + errorId));
        
        error.setIsResolved(true);
        error.setResolutionNotes(resolutionNotes);
        
        BulkUploadError savedError = errorRepository.save(error);
        return convertToDTO(savedError);
    }
    
    // Get error statistics
    public ErrorStatistics getErrorStatistics() {
        ErrorStatistics stats = new ErrorStatistics();
        
        stats.setTotalErrors(errorRepository.count());
        stats.setUnresolvedErrors(Long.valueOf(errorRepository.findByIsResolvedFalseOrderByCreatedAtDesc().size()));
        
        stats.setMediaErrors(errorRepository.countByUploadType("MEDIA"));
        stats.setContentErrors(errorRepository.countByUploadType("CONTENT"));
        stats.setUploadErrors(errorRepository.countByUploadType("UPLOAD"));
        
        stats.setUnresolvedMediaErrors(errorRepository.countUnresolvedByUploadType("MEDIA"));
        stats.setUnresolvedContentErrors(errorRepository.countUnresolvedByUploadType("CONTENT"));
        stats.setUnresolvedUploadErrors(errorRepository.countUnresolvedByUploadType("UPLOAD"));
        
        return stats;
    }
    
    // Delete error
    public void deleteError(Long errorId) {
        errorRepository.deleteById(errorId);
    }
    
    // Convert entity to DTO
    private BulkUploadErrorDTO convertToDTO(BulkUploadError error) {
        BulkUploadErrorDTO dto = new BulkUploadErrorDTO();
        dto.setId(error.getId());
        dto.setUploadType(error.getUploadType());
        dto.setBatchId(error.getBatchId());
        dto.setRowNumber(error.getRowNumber());
        dto.setRawData(error.getRawData());
        dto.setErrorType(error.getErrorType());
        dto.setErrorMessage(error.getErrorMessage());
        dto.setFieldName(error.getFieldName());
        dto.setAttemptedValue(error.getAttemptedValue());
        dto.setSuggestions(error.getSuggestions());
        dto.setIsResolved(error.getIsResolved());
        dto.setResolutionNotes(error.getResolutionNotes());
        dto.setCreatedAt(error.getCreatedAt());
        dto.setUpdatedAt(error.getUpdatedAt());
        return dto;
    }
    
    // Inner class for error statistics
    public static class ErrorStatistics {
        private Long totalErrors = 0L;
        private Long unresolvedErrors = 0L;
        private Long mediaErrors = 0L;
        private Long contentErrors = 0L;
        private Long uploadErrors = 0L;
        private Long unresolvedMediaErrors = 0L;
        private Long unresolvedContentErrors = 0L;
        private Long unresolvedUploadErrors = 0L;
        
        // Getters and Setters
        public Long getTotalErrors() { return totalErrors; }
        public void setTotalErrors(Long totalErrors) { this.totalErrors = totalErrors; }
        
        public Long getUnresolvedErrors() { return unresolvedErrors; }
        public void setUnresolvedErrors(Long unresolvedErrors) { this.unresolvedErrors = unresolvedErrors; }
        
        public Long getMediaErrors() { return mediaErrors; }
        public void setMediaErrors(Long mediaErrors) { this.mediaErrors = mediaErrors; }
        
        public Long getContentErrors() { return contentErrors; }
        public void setContentErrors(Long contentErrors) { this.contentErrors = contentErrors; }
        
        public Long getUploadErrors() { return uploadErrors; }
        public void setUploadErrors(Long uploadErrors) { this.uploadErrors = uploadErrors; }
        
        public Long getUnresolvedMediaErrors() { return unresolvedMediaErrors; }
        public void setUnresolvedMediaErrors(Long unresolvedMediaErrors) { this.unresolvedMediaErrors = unresolvedMediaErrors; }
        
        public Long getUnresolvedContentErrors() { return unresolvedContentErrors; }
        public void setUnresolvedContentErrors(Long unresolvedContentErrors) { this.unresolvedContentErrors = unresolvedContentErrors; }
        
        public Long getUnresolvedUploadErrors() { return unresolvedUploadErrors; }
        public void setUnresolvedUploadErrors(Long unresolvedUploadErrors) { this.unresolvedUploadErrors = unresolvedUploadErrors; }
    }
}