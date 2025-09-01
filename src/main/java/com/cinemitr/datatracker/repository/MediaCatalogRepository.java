package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.MediaCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaCatalogRepository extends JpaRepository<MediaCatalog, Long> {
    List<MediaCatalog> findByMediaType(String mediaType);
    List<MediaCatalog> findByLanguage(String language);
    List<MediaCatalog> findByMainGenres(String mainGenres);
    List<MediaCatalog> findByIsDownloaded(Boolean isDownloaded);
    MediaCatalog findByMediaName(String mediaName);
}