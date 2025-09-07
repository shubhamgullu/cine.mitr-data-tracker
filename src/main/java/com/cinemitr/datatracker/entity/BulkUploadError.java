package com.cinemitr.datatracker.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bulk_upload_errors")
public class BulkUploadError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "upload_type", nullable = false)
    private String uploadType; // MEDIA, CONTENT, UPLOAD
    
    @Column(name = "batch_id", nullable = false)
    private String batchId; // Unique identifier for each bulk upload session
    
    @Column(name = "row_number", nullable = false)
    private Integer rowNumber; // Row number in the CSV file
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData; // Original CSV row data
    
    @Column(name = "error_type", nullable = false)
    private String errorType; // VALIDATION_ERROR, DUPLICATE_ERROR, DATABASE_ERROR, etc.
    
    @Column(name = "error_message", columnDefinition = "TEXT", nullable = false)
    private String errorMessage; // Detailed error description
    
    @Column(name = "field_name")
    private String fieldName; // Specific field that caused the error (if applicable)
    
    @Column(name = "attempted_value", columnDefinition = "TEXT")
    private String attemptedValue; // Value that caused the error
    
    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions; // Suggestions for fixing the error
    
    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    // Constructors
    public BulkUploadError() {}
    
    public BulkUploadError(String uploadType, String batchId, Integer rowNumber, 
                          String rawData, String errorType, String errorMessage) {
        this.uploadType = uploadType;
        this.batchId = batchId;
        this.rowNumber = rowNumber;
        this.rawData = rawData;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
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