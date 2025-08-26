package com.cinemitr.controller;

import com.cinemitr.model.UploadCatalog;
import com.cinemitr.repository.UploadCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/upload-catalog")
public class UploadCatalogController {
    
    @Autowired
    private UploadCatalogRepository uploadCatalogRepository;
    
    @GetMapping
    public List<UploadCatalog> getAllUploadCatalogs() {
        return uploadCatalogRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UploadCatalog> getUploadCatalogById(@PathVariable Long id) {
        Optional<UploadCatalog> uploadCatalog = uploadCatalogRepository.findById(id);
        return uploadCatalog.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public UploadCatalog createUploadCatalog(@RequestBody UploadCatalog uploadCatalog) {
        return uploadCatalogRepository.save(uploadCatalog);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UploadCatalog> updateUploadCatalog(@PathVariable Long id, @RequestBody UploadCatalog uploadCatalogDetails) {
        Optional<UploadCatalog> optionalUploadCatalog = uploadCatalogRepository.findById(id);
        
        if (optionalUploadCatalog.isPresent()) {
            UploadCatalog uploadCatalog = optionalUploadCatalog.get();
            uploadCatalog.setContentCatalogLink(uploadCatalogDetails.getContentCatalogLink());
            uploadCatalog.setMediaCatalogType(uploadCatalogDetails.getMediaCatalogType());
            uploadCatalog.setMediaCatalogName(uploadCatalogDetails.getMediaCatalogName());
            uploadCatalog.setContentCatalogLocation(uploadCatalogDetails.getContentCatalogLocation());
            uploadCatalog.setUploadCatalogLocation(uploadCatalogDetails.getUploadCatalogLocation());
            uploadCatalog.setUploadStatus(uploadCatalogDetails.getUploadStatus());
            uploadCatalog.setUploadCatalogCaption(uploadCatalogDetails.getUploadCatalogCaption());
            uploadCatalog.setUpdatedBy("system");
            
            return ResponseEntity.ok(uploadCatalogRepository.save(uploadCatalog));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUploadCatalog(@PathVariable Long id) {
        Optional<UploadCatalog> uploadCatalog = uploadCatalogRepository.findById(id);
        
        if (uploadCatalog.isPresent()) {
            uploadCatalogRepository.delete(uploadCatalog.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public List<UploadCatalog> getUploadCatalogsByStatus(@PathVariable UploadCatalog.UploadStatus status) {
        return uploadCatalogRepository.findByUploadStatusOrderByCreatedOnDesc(status);
    }
    
    @GetMapping("/type/{type}")
    public List<UploadCatalog> getUploadCatalogsByType(@PathVariable UploadCatalog.MediaType type) {
        return uploadCatalogRepository.findByMediaCatalogTypeOrderByCreatedOnDesc(type);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UploadCatalog>> searchUploadCatalogs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String link) {
        try {
            System.out.println("Upload catalog search - name: " + name + ", type: " + type + ", status: " + status + ", link: " + link);
            
            List<UploadCatalog> results = uploadCatalogRepository.findAll();
            
            // Apply filters
            if (name != null && !name.trim().isEmpty()) {
                results = results.stream()
                    .filter(upload -> upload.getMediaCatalogName() != null && 
                            upload.getMediaCatalogName().toLowerCase().contains(name.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (type != null && !type.trim().isEmpty()) {
                try {
                    UploadCatalog.MediaType mediaType = UploadCatalog.MediaType.valueOf(type.toUpperCase().replace("-", "_"));
                    results = results.stream()
                        .filter(upload -> upload.getMediaCatalogType() == mediaType)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid upload media type: " + type);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            if (status != null && !status.trim().isEmpty()) {
                try {
                    UploadCatalog.UploadStatus uploadStatus = UploadCatalog.UploadStatus.valueOf(status.toUpperCase().replace("-", "_"));
                    results = results.stream()
                        .filter(upload -> upload.getUploadStatus() == uploadStatus)
                        .collect(java.util.stream.Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid upload status: " + status);
                    return ResponseEntity.badRequest().body(java.util.Collections.emptyList());
                }
            }
            
            if (link != null && !link.trim().isEmpty()) {
                results = results.stream()
                    .filter(upload -> upload.getContentCatalogLink() != null && 
                            upload.getContentCatalogLink().toLowerCase().contains(link.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Sort by created date desc
            results.sort((a, b) -> {
                if (a.getCreatedOn() == null && b.getCreatedOn() == null) return 0;
                if (a.getCreatedOn() == null) return 1;
                if (b.getCreatedOn() == null) return -1;
                return b.getCreatedOn().compareTo(a.getCreatedOn());
            });
            
            System.out.println("Upload catalog search results count: " + results.size());
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            System.err.println("Upload catalog search error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.emptyList());
        }
    }
    
    @GetMapping("/link")
    public List<UploadCatalog> getUploadCatalogsByLink(@RequestParam String link) {
        return uploadCatalogRepository.findByContentCatalogLink(link);
    }
    
    @GetMapping("/stats/count-by-status/{status}")
    public ResponseEntity<Long> getCountByUploadStatus(@PathVariable UploadCatalog.UploadStatus status) {
        Long count = uploadCatalogRepository.countByUploadStatus(status);
        return ResponseEntity.ok(count);
    }
}