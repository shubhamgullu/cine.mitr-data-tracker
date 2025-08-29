package com.cinemitr.repository;

import com.cinemitr.model.MediaCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaCatalogRepository extends JpaRepository<MediaCatalog, Long> {
    
    List<MediaCatalog> findByTypeOrderByCreatedOnDesc(MediaCatalog.MediaType type);
    
    List<MediaCatalog> findByNameContainingIgnoreCaseOrderByCreatedOnDesc(String name);
    
    List<MediaCatalog> findByDownloadStatusOrderByCreatedOnDesc(MediaCatalog.DownloadStatus downloadStatus);
    
    @Query("SELECT m FROM MediaCatalog m WHERE m.platform = :platform ORDER BY m.createdOn DESC")
    List<MediaCatalog> findByPlatform(@Param("platform") String platform);
    
    @Query("SELECT COUNT(m) FROM MediaCatalog m WHERE m.type = :type")
    Long countByType(@Param("type") MediaCatalog.MediaType type);
    
    // New methods for language, main genre, and sub-genres
    List<MediaCatalog> findByLanguageOrderByCreatedOnDesc(String language);
    
    List<MediaCatalog> findByMainGenreOrderByCreatedOnDesc(String mainGenre);
    
    List<MediaCatalog> findByLanguageAndMainGenreOrderByCreatedOnDesc(String language, String mainGenre);
    
    @Query("SELECT m FROM MediaCatalog m WHERE m.subGenres LIKE CONCAT('%', :subGenre, '%') ORDER BY m.createdOn DESC")
    List<MediaCatalog> findBySubGenresContaining(@Param("subGenre") String subGenre);
    
    @Query("SELECT DISTINCT m.language FROM MediaCatalog m WHERE m.language IS NOT NULL AND m.language != '' ORDER BY m.language")
    List<String> findAllDistinctLanguages();
    
    @Query("SELECT DISTINCT m.mainGenre FROM MediaCatalog m WHERE m.mainGenre IS NOT NULL AND m.mainGenre != '' ORDER BY m.mainGenre")
    List<String> findAllDistinctMainGenres();
    
    // Check for duplicate name + language combination
    Optional<MediaCatalog> findByNameAndLanguage(String name, String language);
    
    @Query("SELECT m FROM MediaCatalog m WHERE m.name = :name AND (m.language = :language OR (m.language IS NULL AND :language IS NULL))")
    Optional<MediaCatalog> findByNameAndLanguageNullSafe(@Param("name") String name, @Param("language") String language);
}