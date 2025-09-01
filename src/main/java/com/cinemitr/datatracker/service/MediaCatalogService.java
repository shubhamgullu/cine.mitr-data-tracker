package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.MediaCatalogDTO;
import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.repository.MediaCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediaCatalogService {
    
    @Autowired
    private MediaCatalogRepository mediaRepository;

    public List<MediaCatalogDTO> getAllMedia() {
        return mediaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MediaCatalogDTO> getMediaById(Long id) {
        return mediaRepository.findById(id)
                .map(this::convertToDTO);
    }

    public MediaCatalogDTO saveMedia(MediaCatalogDTO mediaDTO) {
        MediaCatalog media = convertToEntity(mediaDTO);
        MediaCatalog savedMedia = mediaRepository.save(media);
        return convertToDTO(savedMedia);
    }

    public MediaCatalogDTO updateMedia(Long id, MediaCatalogDTO mediaDTO) {
        MediaCatalog media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));
        
        updateEntityFromDTO(media, mediaDTO);
        MediaCatalog updatedMedia = mediaRepository.save(media);
        return convertToDTO(updatedMedia);
    }

    public void deleteMedia(Long id) {
        mediaRepository.deleteById(id);
    }

    private MediaCatalogDTO convertToDTO(MediaCatalog media) {
        MediaCatalogDTO dto = new MediaCatalogDTO();
        dto.setId(media.getId());
        dto.setMediaType(media.getMediaType());
        dto.setMediaName(media.getMediaName());
        dto.setLanguage(media.getLanguage());
        dto.setMainGenres(media.getMainGenres());
        dto.setSubGenres(media.getSubGenres());
        dto.setIsDownloaded(media.getIsDownloaded() ? "Yes" : "No");
        dto.setDownloadPath(media.getDownloadPath() != null ? media.getDownloadPath().getPath() : "");
        dto.setAvailableOn(media.getAvailableOn());
        return dto;
    }

    private MediaCatalog convertToEntity(MediaCatalogDTO dto) {
        MediaCatalog media = new MediaCatalog();
        updateEntityFromDTO(media, dto);
        return media;
    }

    private void updateEntityFromDTO(MediaCatalog media, MediaCatalogDTO dto) {
        media.setMediaType(dto.getMediaType());
        media.setMediaName(dto.getMediaName());
        media.setLanguage(dto.getLanguage());
        media.setMainGenres(dto.getMainGenres());
        media.setSubGenres(dto.getSubGenres());
        media.setIsDownloaded("Yes".equalsIgnoreCase(dto.getIsDownloaded()));
        media.setAvailableOn(dto.getAvailableOn());
    }
}