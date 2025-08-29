package com.cinemitr.controller;

import com.cinemitr.model.MediaCatalog;
import com.cinemitr.repository.MediaCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/media-catalog")
public class MediaCatalogController {
    
    @Autowired
    private MediaCatalogRepository mediaCatalogRepository;
    
    @GetMapping
    public List<MediaCatalog> getAllMediaCatalogs(@RequestParam(required = false) Integer limit,
                                                 @RequestParam(required = false) String sort) {
        if (limit != null && sort != null) {
            // Parse sort parameter (e.g., "updatedOn:desc")
            String[] sortParts = sort.split(":");
            String sortField = sortParts[0];
            String sortDirection = sortParts.length > 1 ? sortParts[1] : "asc";
            
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(direction, sortField));
            
            return mediaCatalogRepository.findAll(pageRequest).getContent();
        }
        return mediaCatalogRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MediaCatalog> getMediaCatalogById(@PathVariable Long id) {
        Optional<MediaCatalog> mediaCatalog = mediaCatalogRepository.findById(id);
        return mediaCatalog.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createMediaCatalog(@RequestBody MediaCatalog mediaCatalog) {
        try {
            // Set default values if null
            if (mediaCatalog.getDownloadStatus() == null) {
                mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.NOT_DOWNLOADED);
            }
            
            // Check for duplicates
            Optional<MediaCatalog> existingMedia = mediaCatalogRepository.findByNameAndLanguageNullSafe(
                mediaCatalog.getName(), mediaCatalog.getLanguage());
            
            if (existingMedia.isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "A media catalog entry with this name and language already exists");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            MediaCatalog savedMedia = mediaCatalogRepository.save(mediaCatalog);
            return ResponseEntity.ok(savedMedia);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error creating media catalog: " + e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MediaCatalog> updateMediaCatalog(@PathVariable Long id, @RequestBody MediaCatalog mediaCatalogDetails) {
        Optional<MediaCatalog> optionalMediaCatalog = mediaCatalogRepository.findById(id);
        
        if (optionalMediaCatalog.isPresent()) {
            MediaCatalog mediaCatalog = optionalMediaCatalog.get();
            mediaCatalog.setName(mediaCatalogDetails.getName());
            mediaCatalog.setType(mediaCatalogDetails.getType());
            mediaCatalog.setPlatform(mediaCatalogDetails.getPlatform());
            mediaCatalog.setDownloadStatus(mediaCatalogDetails.getDownloadStatus());
            mediaCatalog.setLocation(mediaCatalogDetails.getLocation());
            mediaCatalog.setDescription(mediaCatalogDetails.getDescription());
            mediaCatalog.setFunFacts(mediaCatalogDetails.getFunFacts());
            mediaCatalog.setLanguage(mediaCatalogDetails.getLanguage());
            mediaCatalog.setMainGenre(mediaCatalogDetails.getMainGenre());
            mediaCatalog.setSubGenres(mediaCatalogDetails.getSubGenres());
            mediaCatalog.setUpdatedBy("system");
            
            return ResponseEntity.ok(mediaCatalogRepository.save(mediaCatalog));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMediaCatalog(@PathVariable Long id) {
        Optional<MediaCatalog> mediaCatalog = mediaCatalogRepository.findById(id);
        
        if (mediaCatalog.isPresent()) {
            mediaCatalogRepository.delete(mediaCatalog.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/type/{type}")
    public List<MediaCatalog> getMediaCatalogsByType(@PathVariable MediaCatalog.MediaType type) {
        return mediaCatalogRepository.findByTypeOrderByCreatedOnDesc(type);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<MediaCatalog>> searchMediaCatalogs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String mainGenre,
            @RequestParam(required = false) String subGenres) {
        try {
            System.out.println("Media catalog search - name: " + name + ", type: " + type + ", status: " + status + 
                             ", language: " + language + ", mainGenre: " + mainGenre + ", subGenres: " + subGenres);
            
            List<MediaCatalog> results = mediaCatalogRepository.findAll();
            
            // Apply filters
            if (name != null && !name.trim().isEmpty()) {
                results = results.stream()
                    .filter(media -> media.getName() != null && media.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (type != null && !type.trim().isEmpty()) {
                try {
                    String typeFormatted = type.toUpperCase().replace("-", "_");
                    System.out.println("Formatted type: " + typeFormatted);
                    MediaCatalog.MediaType mediaType = MediaCatalog.MediaType.valueOf(typeFormatted);
                    results = results.stream()
                        .filter(media -> media.getType() == mediaType)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid media type: " + type);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            if (status != null && !status.trim().isEmpty()) {
                try {
                    String statusFormatted = status.toUpperCase().replace("-", "_");
                    System.out.println("Formatted status: " + statusFormatted);
                    MediaCatalog.DownloadStatus downloadStatus = MediaCatalog.DownloadStatus.valueOf(statusFormatted);
                    results = results.stream()
                        .filter(media -> media.getDownloadStatus() == downloadStatus)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid download status: " + status);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            // Filter by language
            if (language != null && !language.trim().isEmpty()) {
                results = results.stream()
                    .filter(media -> media.getLanguage() != null && media.getLanguage().toLowerCase().contains(language.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter by main genre
            if (mainGenre != null && !mainGenre.trim().isEmpty()) {
                results = results.stream()
                    .filter(media -> media.getMainGenre() != null && media.getMainGenre().toLowerCase().contains(mainGenre.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter by sub genres
            if (subGenres != null && !subGenres.trim().isEmpty()) {
                results = results.stream()
                    .filter(media -> media.getSubGenres() != null && media.getSubGenres().toLowerCase().contains(subGenres.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Sort by updated date desc
            results.sort((a, b) -> {
                if (a.getUpdatedOn() == null && b.getUpdatedOn() == null) return 0;
                if (a.getUpdatedOn() == null) return 1;
                if (b.getUpdatedOn() == null) return -1;
                return b.getUpdatedOn().compareTo(a.getUpdatedOn());
            });
            
            System.out.println("Media catalog search results count: " + results.size());
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            System.err.println("Media catalog search error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.emptyList());
        }
    }
    
    @GetMapping("/download-status/{status}")
    public List<MediaCatalog> getMediaCatalogsByDownloadStatus(@PathVariable MediaCatalog.DownloadStatus status) {
        return mediaCatalogRepository.findByDownloadStatusOrderByCreatedOnDesc(status);
    }
    
    @GetMapping("/platform/{platform}")
    public List<MediaCatalog> getMediaCatalogsByPlatform(@PathVariable String platform) {
        return mediaCatalogRepository.findByPlatform(platform);
    }
    
    @GetMapping("/stats/count-by-type/{type}")
    public ResponseEntity<Long> getCountByType(@PathVariable MediaCatalog.MediaType type) {
        Long count = mediaCatalogRepository.countByType(type);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllMediaCatalogNames() {
        List<String> names = mediaCatalogRepository.findAll()
            .stream()
            .map(MediaCatalog::getName)
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(names);
    }
    
    @GetMapping("/language/{language}")
    public List<MediaCatalog> getMediaCatalogsByLanguage(@PathVariable String language) {
        return mediaCatalogRepository.findByLanguageOrderByCreatedOnDesc(language);
    }
    
    @GetMapping("/main-genre/{genre}")
    public List<MediaCatalog> getMediaCatalogsByMainGenre(@PathVariable String genre) {
        return mediaCatalogRepository.findByMainGenreOrderByCreatedOnDesc(genre);
    }
    
    @GetMapping("/sub-genre/{subGenre}")
    public List<MediaCatalog> getMediaCatalogsBySubGenre(@PathVariable String subGenre) {
        return mediaCatalogRepository.findBySubGenresContaining(subGenre);
    }
    
    @GetMapping("/languages")
    public ResponseEntity<List<String>> getAllLanguages() {
        List<String> languages = mediaCatalogRepository.findAllDistinctLanguages();
        return ResponseEntity.ok(languages);
    }
    
    @GetMapping("/main-genres")
    public ResponseEntity<List<String>> getAllMainGenres() {
        List<String> genres = mediaCatalogRepository.findAllDistinctMainGenres();
        return ResponseEntity.ok(genres);
    }
}