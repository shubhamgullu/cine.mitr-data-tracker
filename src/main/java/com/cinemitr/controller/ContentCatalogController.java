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
    public List<ContentCatalog> searchContentCatalogs(@RequestParam String name) {
        return contentCatalogRepository.findByMediaCatalogNameContainingIgnoreCaseOrderByCreatedOnDesc(name);
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
}