package com.cinemitr.repository;

import com.cinemitr.model.ContentCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentCatalogRepository extends JpaRepository<ContentCatalog, Long> {
    
    List<ContentCatalog> findByStatusOrderByCreatedOnDesc(ContentCatalog.ContentStatus status);
    
    List<ContentCatalog> findByMediaCatalogTypeOrderByCreatedOnDesc(ContentCatalog.MediaType mediaCatalogType);
    
    List<ContentCatalog> findByPriorityOrderByCreatedOnDesc(ContentCatalog.Priority priority);
    
    List<ContentCatalog> findByMediaCatalogNameContainingIgnoreCaseOrderByCreatedOnDesc(String mediaCatalogName);
    
    @Query("SELECT c FROM ContentCatalog c WHERE c.uploadContentStatus = :uploadStatus ORDER BY c.createdOn DESC")
    List<ContentCatalog> findByUploadContentStatus(@Param("uploadStatus") ContentCatalog.UploadContentStatus uploadStatus);
    
    @Query("SELECT COUNT(c) FROM ContentCatalog c WHERE c.status = :status")
    Long countByStatus(@Param("status") ContentCatalog.ContentStatus status);
}