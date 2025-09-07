package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.BulkUploadError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BulkUploadErrorRepository extends JpaRepository<BulkUploadError, Long> {
    
    // Find all errors for a specific batch
    List<BulkUploadError> findByBatchIdOrderByRowNumberAsc(String batchId);
    
    // Find all errors by upload type
    List<BulkUploadError> findByUploadTypeOrderByCreatedAtDesc(String uploadType);
    
    // Find unresolved errors
    List<BulkUploadError> findByIsResolvedFalseOrderByCreatedAtDesc();
    
    // Find unresolved errors by upload type
    List<BulkUploadError> findByUploadTypeAndIsResolvedFalseOrderByCreatedAtDesc(String uploadType);
    
    // Find errors by error type
    List<BulkUploadError> findByErrorTypeOrderByCreatedAtDesc(String errorType);
    
    // Find recent errors (last 7 days)
    @Query("SELECT e FROM BulkUploadError e WHERE e.createdAt >= :fromDate ORDER BY e.createdAt DESC")
    List<BulkUploadError> findRecentErrors(@Param("fromDate") Date fromDate);
    
    // Count total errors by upload type
    @Query("SELECT COUNT(e) FROM BulkUploadError e WHERE e.uploadType = :uploadType")
    Long countByUploadType(@Param("uploadType") String uploadType);
    
    // Count unresolved errors by upload type
    @Query("SELECT COUNT(e) FROM BulkUploadError e WHERE e.uploadType = :uploadType AND e.isResolved = false")
    Long countUnresolvedByUploadType(@Param("uploadType") String uploadType);
    
    // Find errors with suggestions available
    @Query("SELECT e FROM BulkUploadError e WHERE e.suggestions IS NOT NULL AND e.suggestions != '' ORDER BY e.createdAt DESC")
    List<BulkUploadError> findErrorsWithSuggestions();
    
    // Delete old resolved errors (cleanup)
    @Query("DELETE FROM BulkUploadError e WHERE e.isResolved = true AND e.createdAt < :beforeDate")
    void deleteOldResolvedErrors(@Param("beforeDate") Date beforeDate);
}