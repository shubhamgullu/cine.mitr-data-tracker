package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.ContentCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentCatalogRepository extends JpaRepository<ContentCatalog, Long> {
    List<ContentCatalog> findByStatus(String status);
    List<ContentCatalog> findByPriority(String priority);
    List<ContentCatalog> findByLocalStatus(String localStatus);
    @Query("SELECT c FROM ContentCatalog c JOIN c.mediaList m WHERE m.id = :mediaId")
    List<ContentCatalog> findByMediaId(@Param("mediaId") Long mediaId);
}