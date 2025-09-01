package com.cinemitr.datatracker.controller;

import com.cinemitr.datatracker.dto.ContentCatalogDTO;
import com.cinemitr.datatracker.service.ContentCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentCatalogController {
    
    @Autowired
    private ContentCatalogService contentService;

    @GetMapping
    public ResponseEntity<List<ContentCatalogDTO>> getAllContent() {
        List<ContentCatalogDTO> content = contentService.getAllContent();
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentCatalogDTO> getContentById(@PathVariable Long id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContentCatalogDTO> createContent(@RequestBody ContentCatalogDTO contentDTO) {
        ContentCatalogDTO savedContent = contentService.saveContent(contentDTO);
        return ResponseEntity.ok(savedContent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentCatalogDTO> updateContent(@PathVariable Long id, @RequestBody ContentCatalogDTO contentDTO) {
        try {
            ContentCatalogDTO updatedContent = contentService.updateContent(id, contentDTO);
            return ResponseEntity.ok(updatedContent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getContentCount() {
        List<ContentCatalogDTO> content = contentService.getAllContent();
        return ResponseEntity.ok((long) content.size());
    }
}