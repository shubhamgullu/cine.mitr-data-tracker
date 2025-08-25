package com.cinemitr.repository;

import com.cinemitr.model.UploadCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UploadCatalogRepository extends JpaRepository<UploadCatalog, Long> {
    
    List<UploadCatalog> findByUploadStatusOrderByCreatedOnDesc(UploadCatalog.UploadStatus uploadStatus);
    
    List<UploadCatalog> findByMediaCatalogTypeOrderByCreatedOnDesc(UploadCatalog.MediaType mediaCatalogType);
    
    List<UploadCatalog> findByMediaCatalogNameContainingIgnoreCaseOrderByCreatedOnDesc(String mediaCatalogName);
    
    @Query("SELECT u FROM UploadCatalog u WHERE u.contentCatalogLink = :link ORDER BY u.createdOn DESC")
    List<UploadCatalog> findByContentCatalogLink(@Param("link") String link);
    
    @Query("SELECT COUNT(u) FROM UploadCatalog u WHERE u.uploadStatus = :status")
    Long countByUploadStatus(@Param("status") UploadCatalog.UploadStatus status);
    
    Optional<UploadCatalog> findByLinkedContentCatalogId(Long linkedContentCatalogId);
}