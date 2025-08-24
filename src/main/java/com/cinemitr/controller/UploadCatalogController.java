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
    public List<UploadCatalog> searchUploadCatalogs(@RequestParam String name) {
        return uploadCatalogRepository.findByMediaCatalogNameContainingIgnoreCaseOrderByCreatedOnDesc(name);
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