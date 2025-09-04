package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.UploadCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadCatalogRepository extends JpaRepository<UploadCatalog, Long> {
    List<UploadCatalog> findByStatus(String status);
    @Query("SELECT u FROM UploadCatalog u JOIN u.mediaList m WHERE m.id = :mediaId")
    List<UploadCatalog> findByMediaId(@Param("mediaId") Long mediaId);
}