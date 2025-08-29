package com.cinemitr.repository;

import com.cinemitr.model.ContentCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    
    Optional<ContentCatalog> findByLinkedUploadCatalogId(Long linkedUploadCatalogId);
    
    Optional<ContentCatalog> findByLink(String link);
    
    boolean existsByLink(String link);
    
    List<ContentCatalog> findByMediaCatalogNameContainingIgnoreCaseAndLink(String mediaCatalogName, String link);
    
    @Query("SELECT c FROM ContentCatalog c WHERE c.mediaCatalogName LIKE %:name% OR c.link = :link ORDER BY c.createdOn DESC")
    List<ContentCatalog> findByMediaCatalogNameOrLink(@Param("name") String name, @Param("link") String link);
    
    @Query("SELECT c FROM ContentCatalog c WHERE c.mediaCatalogName LIKE %:name% ORDER BY c.createdOn DESC")
    List<ContentCatalog> findByMediaCatalogNameContaining(@Param("name") String name);
    
    @Query("SELECT c FROM ContentCatalog c WHERE c.mediaCatalogName LIKE %:name% AND c.mediaCatalogType = :type ORDER BY c.createdOn DESC")
    List<ContentCatalog> findByMediaCatalogNameContainingAndMediaCatalogType(@Param("name") String name, @Param("type") ContentCatalog.MediaType type);
    
    @Query("SELECT c FROM ContentCatalog c WHERE " +
           "(c.mediaCatalogName LIKE %:name% OR c.mediaCatalogName IN :nameList) " +
           "AND (:type IS NULL OR c.mediaCatalogType = :type) " +
           "ORDER BY c.createdOn DESC")
    List<ContentCatalog> findByMediaCatalogNamesAndType(@Param("name") String name, @Param("nameList") List<String> nameList, @Param("type") ContentCatalog.MediaType type);
}