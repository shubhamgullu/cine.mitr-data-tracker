package com.cinemitr.datatracker.controller;

import com.cinemitr.datatracker.dto.MediaCatalogDTO;
import com.cinemitr.datatracker.service.MediaCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*")
public class MediaCatalogController {
    
    @Autowired
    private MediaCatalogService mediaService;

    @GetMapping
    public ResponseEntity<List<MediaCatalogDTO>> getAllMedia() {
        List<MediaCatalogDTO> media = mediaService.getAllMedia();
        return ResponseEntity.ok(media);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaCatalogDTO> getMediaById(@PathVariable Long id) {
        return mediaService.getMediaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMedia(@RequestBody MediaCatalogDTO mediaDTO) {
        try {
            MediaCatalogDTO savedMedia = mediaService.saveMedia(mediaDTO);
            return ResponseEntity.ok(savedMedia);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace(); // Add logging for debugging
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create media: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MediaCatalogDTO> updateMedia(@PathVariable Long id, @RequestBody MediaCatalogDTO mediaDTO) {
        try {
            MediaCatalogDTO updatedMedia = mediaService.updateMedia(id, mediaDTO);
            return ResponseEntity.ok(updatedMedia);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getMediaCount() {
        List<MediaCatalogDTO> media = mediaService.getAllMedia();
        return ResponseEntity.ok((long) media.size());
    }

    @GetMapping("/genres")
    public ResponseEntity<List<String>> getUniqueGenres() {
        List<String> genres = mediaService.getUniqueMainGenres();
        return ResponseEntity.ok(genres);
    }
}