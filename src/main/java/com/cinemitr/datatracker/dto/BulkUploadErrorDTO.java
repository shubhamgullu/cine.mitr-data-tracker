package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class BulkUploadErrorDTO {
    private Long id;
    
    @JsonProperty("upload_type")
    private String uploadType;
    
    @JsonProperty("batch_id") 
    private String batchId;
    
    @JsonProperty("row_number")
    private Integer rowNumber;
    
    @JsonProperty("raw_data")
    private String rawData;
    
    @JsonProperty("error_type")
    private String errorType;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    @JsonProperty("field_name")
    private String fieldName;
    
    @JsonProperty("attempted_value")
    private String attemptedValue;
    
    private String suggestions;
    
    @JsonProperty("is_resolved")
    private Boolean isResolved;
    
    @JsonProperty("resolution_notes")
    private String resolutionNotes;
    
    @JsonProperty("created_at")
    private Date createdAt;
    
    @JsonProperty("updated_at")
    private Date updatedAt;
    
    // Constructors
    public BulkUploadErrorDTO() {}
    
    public BulkUploadErrorDTO(String uploadType, String batchId, Integer rowNumber, 
                             String errorType, String errorMessage) {
        this.uploadType = uploadType;
        this.batchId = batchId;
        this.rowNumber = rowNumber;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.isResolved = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUploadType() {
        return uploadType;
    }
    
    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }
    
    public String getBatchId() {
        return batchId;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    public Integer getRowNumber() {
        return rowNumber;
    }
    
    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }
    
    public String getRawData() {
        return rawData;
    }
    
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getAttemptedValue() {
        return attemptedValue;
    }
    
    public void setAttemptedValue(String attemptedValue) {
        this.attemptedValue = attemptedValue;
    }
    
    public String getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }
    
    public Boolean getIsResolved() {
        return isResolved;
    }
    
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }
    
    public String getResolutionNotes() {
        return resolutionNotes;
    }
    
    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}