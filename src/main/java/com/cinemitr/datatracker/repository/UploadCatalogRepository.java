package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.UploadCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadCatalogRepository extends JpaRepository<UploadCatalog, Long> {
    List<UploadCatalog> findByStatus(String status);
    List<UploadCatalog> findByMediaId(Long mediaId);
}