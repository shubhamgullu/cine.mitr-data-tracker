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
    
    Optional<UploadCatalog> findByLinkedContentCatalogLink(String linkedContentCatalogLink);
    
    // Legacy compatibility method - finds by linked content catalog link using the link directly
    @Query("SELECT u FROM UploadCatalog u JOIN ContentCatalog c ON u.linkedContentCatalogLink = c.link WHERE c.link = :contentLink")
    Optional<UploadCatalog> findByLinkedContentCatalogId(@Param("contentLink") String contentLink);
    
    @Query("SELECT u FROM UploadCatalog u WHERE u.mediaCatalogName LIKE %:name% ORDER BY u.createdOn DESC")
    List<UploadCatalog> findByMediaCatalogNameContaining(@Param("name") String name);
    
    @Query("SELECT u FROM UploadCatalog u WHERE u.mediaCatalogName LIKE %:name% AND u.mediaCatalogType = :type ORDER BY u.createdOn DESC")
    List<UploadCatalog> findByMediaCatalogNameContainingAndMediaCatalogType(@Param("name") String name, @Param("type") UploadCatalog.MediaType type);
}