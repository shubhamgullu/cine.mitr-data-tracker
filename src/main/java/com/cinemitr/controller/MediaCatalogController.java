package com.cinemitr.controller;

import com.cinemitr.model.MediaCatalog;
import com.cinemitr.repository.MediaCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public MediaCatalog createMediaCatalog(@RequestBody MediaCatalog mediaCatalog) {
        // Set default values if null
        if (mediaCatalog.getDownloadStatus() == null) {
            mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.NOT_DOWNLOADED);
        }
        return mediaCatalogRepository.save(mediaCatalog);
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
            @RequestParam(required = false) String status) {
        try {
            System.out.println("Media catalog search - name: " + name + ", type: " + type + ", status: " + status);
            
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
}