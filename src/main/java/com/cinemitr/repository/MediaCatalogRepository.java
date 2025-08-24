package com.cinemitr.repository;

import com.cinemitr.model.MediaCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaCatalogRepository extends JpaRepository<MediaCatalog, Long> {
    
    List<MediaCatalog> findByTypeOrderByCreatedOnDesc(MediaCatalog.MediaType type);
    
    List<MediaCatalog> findByNameContainingIgnoreCaseOrderByCreatedOnDesc(String name);
    
    List<MediaCatalog> findByDownloadStatusOrderByCreatedOnDesc(MediaCatalog.DownloadStatus downloadStatus);
    
    @Query("SELECT m FROM MediaCatalog m WHERE m.platform = :platform ORDER BY m.createdOn DESC")
    List<MediaCatalog> findByPlatform(@Param("platform") String platform);
    
    @Query("SELECT COUNT(m) FROM MediaCatalog m WHERE m.type = :type")
    Long countByType(@Param("type") MediaCatalog.MediaType type);
}