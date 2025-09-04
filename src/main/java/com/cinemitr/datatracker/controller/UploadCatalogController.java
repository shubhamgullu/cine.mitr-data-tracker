package com.cinemitr.datatracker.controller;

import com.cinemitr.datatracker.dto.UploadCatalogDTO;
import com.cinemitr.datatracker.service.UploadCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadCatalogController {
    
    @Autowired
    private UploadCatalogService uploadService;

    @GetMapping
    public ResponseEntity<List<UploadCatalogDTO>> getAllUploads() {
        List<UploadCatalogDTO> uploads = uploadService.getAllUploads();
        return ResponseEntity.ok(uploads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UploadCatalogDTO> getUploadById(@PathVariable Long id) {
        return uploadService.getUploadById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UploadCatalogDTO> createUpload(@RequestBody UploadCatalogDTO uploadDTO) {
        UploadCatalogDTO savedUpload = uploadService.saveUpload(uploadDTO);
        return ResponseEntity.ok(savedUpload);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UploadCatalogDTO> updateUpload(@PathVariable Long id, @RequestBody UploadCatalogDTO uploadDTO) {
        try {
            UploadCatalogDTO updatedUpload = uploadService.updateUpload(id, uploadDTO);
            return ResponseEntity.ok(updatedUpload);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUpload(@PathVariable Long id) {
        uploadService.deleteUpload(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUploadCount() {
        List<UploadCatalogDTO> uploads = uploadService.getAllUploads();
        return ResponseEntity.ok((long) uploads.size());
    }
}