package com.cinemitr.controller;

import com.cinemitr.model.ContentCatalog;
import com.cinemitr.model.UploadCatalog;
import com.cinemitr.repository.ContentCatalogRepository;
import com.cinemitr.repository.UploadCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/content-catalog")
public class ContentCatalogController {
    
    @Autowired
    private ContentCatalogRepository contentCatalogRepository;

    @Autowired
    private UploadCatalogRepository uploadCatalogRepository;

    @GetMapping
    public List<ContentCatalog> getAllContentCatalogs() {
        return contentCatalogRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContentCatalog> getContentCatalogById(@PathVariable String id) {
        Optional<ContentCatalog> contentCatalog = contentCatalogRepository.findById(id);
        return contentCatalog.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/location")
    public ResponseEntity<Map<String, String>> getContentCatalogLocation(@PathVariable String id) {
        Optional<ContentCatalog> contentCatalog = contentCatalogRepository.findById(id);
        if (contentCatalog.isPresent()) {
            Map<String, String> response = new HashMap<>();
            ContentCatalog catalog = contentCatalog.get();
            response.put("location", catalog.getLocation());
            response.put("locationPath", catalog.getLocationPath());
            response.put("mediaCatalogName", catalog.getMediaCatalogName());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
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
    public ResponseEntity<ContentCatalog> updateContentCatalog(@PathVariable String id, @RequestBody ContentCatalog contentCatalogDetails) {
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
    public ResponseEntity<?> deleteContentCatalog(@PathVariable String id) {
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
            // Search by both media catalog name and link
            return contentCatalogRepository.findByMediaCatalogNameOrLink(search.trim(), search.trim());
        }
        return contentCatalogRepository.findAll(
            org.springframework.data.domain.PageRequest.of(0, 50, 
                org.springframework.data.domain.Sort.by("createdOn").descending())
        ).getContent();
    }
    
    @GetMapping("/linked-content")
    public ResponseEntity<List<ContentCatalog>> getLinkedContentForMedia(
            @RequestParam String mediaCatalogName,
            @RequestParam(required = false) String mediaType) {
        try {
            List<ContentCatalog> results;
            
            if (mediaType != null && !mediaType.trim().isEmpty()) {
                // Search with both name and type filtering
                ContentCatalog.MediaType type = ContentCatalog.MediaType.valueOf(mediaType.toUpperCase().replace("-", "_"));
                results = contentCatalogRepository.findByMediaCatalogNameContainingAndMediaCatalogType(mediaCatalogName.trim(), type);
            } else {
                // Search by name only
                results = contentCatalogRepository.findByMediaCatalogNameContaining(mediaCatalogName.trim());
            }
            
            System.out.println("Linked content search - mediaCatalogName: " + mediaCatalogName + 
                             ", mediaType: " + mediaType + ", results: " + results.size());
            
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid media type: " + mediaType);
            return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
        } catch (Exception e) {
            System.err.println("Error searching linked content: " + e.getMessage());
            return ResponseEntity.status(500).body(java.util.Collections.emptyList());
        }
    }
    
    @GetMapping("/all-linked-content")
    public ResponseEntity<Map<String, Object>> getAllLinkedContentForMedia(
            @RequestParam String mediaCatalogName,
            @RequestParam(required = false) String mediaType) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Get content catalog entries
            List<ContentCatalog> contentResults;
            if (mediaType != null && !mediaType.trim().isEmpty()) {
                ContentCatalog.MediaType type = ContentCatalog.MediaType.valueOf(mediaType.toUpperCase().replace("-", "_"));
                contentResults = contentCatalogRepository.findByMediaCatalogNameContainingAndMediaCatalogType(mediaCatalogName.trim(), type);
            } else {
                contentResults = contentCatalogRepository.findByMediaCatalogNameContaining(mediaCatalogName.trim());
            }
            
            // Get upload catalog entries
            List<UploadCatalog> uploadResults;
            if (mediaType != null && !mediaType.trim().isEmpty()) {
                UploadCatalog.MediaType uploadType = UploadCatalog.MediaType.valueOf(mediaType.toUpperCase().replace("-", "_"));
                uploadResults = uploadCatalogRepository.findByMediaCatalogNameContainingAndMediaCatalogType(mediaCatalogName.trim(), uploadType);
            } else {
                uploadResults = uploadCatalogRepository.findByMediaCatalogNameContaining(mediaCatalogName.trim());
            }
            
            System.out.println("All linked content search - mediaCatalogName: " + mediaCatalogName + 
                             ", mediaType: " + mediaType + 
                             ", content results: " + contentResults.size() +
                             ", upload results: " + uploadResults.size());
            
            result.put("contentCatalog", contentResults);
            result.put("uploadCatalog", uploadResults);
            result.put("totalResults", contentResults.size() + uploadResults.size());
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid media type: " + mediaType);
            return ResponseEntity.badRequest().body(createErrorMap("Invalid media type: " + mediaType));
        } catch (Exception e) {
            System.err.println("Error searching all linked content: " + e.getMessage());
            return ResponseEntity.status(500).body(createErrorMap("Error searching linked content: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> createErrorMap(String error) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", error);
        map.put("contentCatalog", java.util.Collections.emptyList());
        map.put("uploadCatalog", java.util.Collections.emptyList());
        map.put("totalResults", 0);
        return map;
    }
}