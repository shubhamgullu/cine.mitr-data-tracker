package com.cinemitr.controller;

import com.cinemitr.model.ContentCatalog;
import com.cinemitr.repository.ContentCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/content-catalog")
public class ContentCatalogController {
    
    @Autowired
    private ContentCatalogRepository contentCatalogRepository;
    
    @GetMapping
    public List<ContentCatalog> getAllContentCatalogs() {
        return contentCatalogRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContentCatalog> getContentCatalogById(@PathVariable Long id) {
        Optional<ContentCatalog> contentCatalog = contentCatalogRepository.findById(id);
        return contentCatalog.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ContentCatalog createContentCatalog(@RequestBody ContentCatalog contentCatalog) {
        // Set default values if null
        if (contentCatalog.getPriority() == null) {
            contentCatalog.setPriority(ContentCatalog.Priority.MEDIUM);
        }
        return contentCatalogRepository.save(contentCatalog);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContentCatalog> updateContentCatalog(@PathVariable Long id, @RequestBody ContentCatalog contentCatalogDetails) {
        Optional<ContentCatalog> optionalContentCatalog = contentCatalogRepository.findById(id);
        
        if (optionalContentCatalog.isPresent()) {
            ContentCatalog contentCatalog = optionalContentCatalog.get();
            contentCatalog.setLink(contentCatalogDetails.getLink());
            contentCatalog.setMediaCatalogType(contentCatalogDetails.getMediaCatalogType());
            contentCatalog.setMediaCatalogName(contentCatalogDetails.getMediaCatalogName());
            contentCatalog.setStatus(contentCatalogDetails.getStatus());
            contentCatalog.setPriority(contentCatalogDetails.getPriority());
            contentCatalog.setLocation(contentCatalogDetails.getLocation());
            contentCatalog.setMetadata(contentCatalogDetails.getMetadata());
            contentCatalog.setLikeStates(contentCatalogDetails.getLikeStates());
            contentCatalog.setCommentStates(contentCatalogDetails.getCommentStates());
            contentCatalog.setUploadContentStatus(contentCatalogDetails.getUploadContentStatus());
            contentCatalog.setUpdatedBy("system");
            
            return ResponseEntity.ok(contentCatalogRepository.save(contentCatalog));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContentCatalog(@PathVariable Long id) {
        Optional<ContentCatalog> contentCatalog = contentCatalogRepository.findById(id);
        
        if (contentCatalog.isPresent()) {
            contentCatalogRepository.delete(contentCatalog.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public List<ContentCatalog> getContentCatalogsByStatus(@PathVariable ContentCatalog.ContentStatus status) {
        return contentCatalogRepository.findByStatusOrderByCreatedOnDesc(status);
    }
    
    @GetMapping("/type/{type}")
    public List<ContentCatalog> getContentCatalogsByType(@PathVariable ContentCatalog.MediaType type) {
        return contentCatalogRepository.findByMediaCatalogTypeOrderByCreatedOnDesc(type);
    }
    
    @GetMapping("/priority/{priority}")
    public List<ContentCatalog> getContentCatalogsByPriority(@PathVariable ContentCatalog.Priority priority) {
        return contentCatalogRepository.findByPriorityOrderByCreatedOnDesc(priority);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ContentCatalog>> searchContentCatalogs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        try {
            System.out.println("Content catalog search - name: " + name + ", type: " + type + ", status: " + status + ", priority: " + priority);
            
            List<ContentCatalog> results = contentCatalogRepository.findAll();
            
            // Apply filters
            if (name != null && !name.trim().isEmpty()) {
                results = results.stream()
                    .filter(content -> content.getMediaCatalogName() != null && 
                            content.getMediaCatalogName().toLowerCase().contains(name.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (type != null && !type.trim().isEmpty()) {
                try {
                    ContentCatalog.MediaType mediaType = ContentCatalog.MediaType.valueOf(type.toUpperCase().replace("-", "_"));
                    results = results.stream()
                        .filter(content -> content.getMediaCatalogType() == mediaType)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid content media type: " + type);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            if (status != null && !status.trim().isEmpty()) {
                try {
                    ContentCatalog.ContentStatus contentStatus = ContentCatalog.ContentStatus.valueOf(status.toUpperCase().replace("-", "_"));
                    results = results.stream()
                        .filter(content -> content.getStatus() == contentStatus)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid content status: " + status);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            if (priority != null && !priority.trim().isEmpty()) {
                try {
                    ContentCatalog.Priority priorityEnum = ContentCatalog.Priority.valueOf(priority.toUpperCase());
                    results = results.stream()
                        .filter(content -> content.getPriority() == priorityEnum)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid priority: " + priority);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            // Sort by created date desc
            results.sort((a, b) -> {
                if (a.getCreatedOn() == null && b.getCreatedOn() == null) return 0;
                if (a.getCreatedOn() == null) return 1;
                if (b.getCreatedOn() == null) return -1;
                return b.getCreatedOn().compareTo(a.getCreatedOn());
            });
            
            System.out.println("Content catalog search results count: " + results.size());
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            System.err.println("Content catalog search error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.emptyList());
        }
    }
    
    @GetMapping("/upload-status/{uploadStatus}")
    public List<ContentCatalog> getContentCatalogsByUploadStatus(@PathVariable ContentCatalog.UploadContentStatus uploadStatus) {
        return contentCatalogRepository.findByUploadContentStatus(uploadStatus);
    }
    
    @GetMapping("/stats/count-by-status/{status}")
    public ResponseEntity<Long> getCountByStatus(@PathVariable ContentCatalog.ContentStatus status) {
        Long count = contentCatalogRepository.countByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/content-blocks")
    public List<ContentCatalog> getContentBlocks(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return contentCatalogRepository.findByMediaCatalogNameContainingIgnoreCaseOrderByCreatedOnDesc(search.trim());
        }
        return contentCatalogRepository.findAll(
            org.springframework.data.domain.PageRequest.of(0, 50, 
                org.springframework.data.domain.Sort.by("createdOn").descending())
        ).getContent();
    }
}