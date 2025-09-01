package com.cinemitr.datatracker.controller;

import com.cinemitr.datatracker.dto.MediaCatalogDTO;
import com.cinemitr.datatracker.service.MediaCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<MediaCatalogDTO> createMedia(@RequestBody MediaCatalogDTO mediaDTO) {
        MediaCatalogDTO savedMedia = mediaService.saveMedia(mediaDTO);
        return ResponseEntity.ok(savedMedia);
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
}