package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.MediaCatalogDTO;
import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.entity.MetadataStatus;
import com.cinemitr.datatracker.enums.PathCategory;
import com.cinemitr.datatracker.repository.MediaCatalogRepository;
import com.cinemitr.datatracker.repository.MetadataStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediaCatalogService {
    
    @Autowired
    private MediaCatalogRepository mediaRepository;
    
    @Autowired
    private MetadataStatusRepository metadataStatusRepository;

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
        // Check if media with same name and type already exists
        MediaCatalog existingMedia = mediaRepository.findByMediaNameAndMediaType(
                mediaDTO.getMediaName(), mediaDTO.getMediaType());
        if (existingMedia != null) {
            throw new IllegalArgumentException(
                    "Media with name '" + mediaDTO.getMediaName() + 
                    "' and type '" + mediaDTO.getMediaType() + "' already exists");
        }
        
        MediaCatalog media = convertToEntity(mediaDTO);
        MediaCatalog savedMedia = mediaRepository.save(media);
        return convertToDTO(savedMedia);
    }

    public MediaCatalogDTO updateMedia(Long id, MediaCatalogDTO mediaDTO) {
        MediaCatalog media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));
        
        // Check if media with same name and type already exists (excluding current record)
        MediaCatalog existingMedia = mediaRepository.findByMediaNameAndMediaType(
                mediaDTO.getMediaName(), mediaDTO.getMediaType());
        if (existingMedia != null && !existingMedia.getId().equals(id)) {
            throw new IllegalArgumentException(
                    "Media with name '" + mediaDTO.getMediaName() + 
                    "' and type '" + mediaDTO.getMediaType() + "' already exists");
        }
        
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
        // Validate required fields
        if (dto.getMediaType() == null || dto.getMediaType().trim().isEmpty()) {
            throw new IllegalArgumentException("Media type is required");
        }
        if (dto.getMediaName() == null || dto.getMediaName().trim().isEmpty()) {
            throw new IllegalArgumentException("Media name is required");
        }
        if (dto.getMainGenres() == null || dto.getMainGenres().trim().isEmpty()) {
            throw new IllegalArgumentException("Main genres is required");
        }
         if (dto.getIsDownloaded().equals("Yes") && (dto.getDownloadPath() == null || dto.getDownloadPath().trim().isEmpty())) {
            throw new IllegalArgumentException("Download Path  is required");
        }
        
        media.setMediaType(dto.getMediaType().trim());
        media.setMediaName(dto.getMediaName().trim());
        media.setLanguage(dto.getLanguage() != null ? dto.getLanguage().trim() : null);
        media.setMainGenres(dto.getMainGenres().trim());
        media.setSubGenres(dto.getSubGenres() != null ? dto.getSubGenres().trim() : null);
        media.setIsDownloaded("Yes".equalsIgnoreCase(dto.getIsDownloaded()));
        media.setAvailableOn(dto.getAvailableOn().trim());
        
        // Handle download path by creating or updating MetadataStatus
        if(dto.getIsDownloaded().equals("Yes") && dto.getDownloadPath()!=null){
        try{
            MetadataStatus mediaDownloadPath = null;
            if (dto.getId()==null){
                mediaDownloadPath = new MetadataStatus();
                mediaDownloadPath.setPathCategory(PathCategory.MEDIA_FILE);
                mediaDownloadPath.setPath(dto.getDownloadPath());
                mediaDownloadPath.setIsAvailable(true);
                mediaDownloadPath.setMetaData("");
                mediaDownloadPath = metadataStatusRepository.save(mediaDownloadPath);
            }else{
                // Get existing MediaCatalog to find current MetadataStatus ID
                MediaCatalog existingMedia = mediaRepository.findById(dto.getId()).orElse(null);
                if (existingMedia != null && existingMedia.getDownloadPath() != null) {
                    // Update existing MetadataStatus
                    mediaDownloadPath = existingMedia.getDownloadPath();
                    mediaDownloadPath.setPath(dto.getDownloadPath());
                    mediaDownloadPath.setIsAvailable(true);
                    // Save the updated MetadataStatus
                    mediaDownloadPath = metadataStatusRepository.save(mediaDownloadPath);
                } else {
                    // Create new MetadataStatus if none exists
                    mediaDownloadPath = new MetadataStatus();
                    mediaDownloadPath.setPathCategory(PathCategory.MEDIA_FILE);
                    mediaDownloadPath.setPath(dto.getDownloadPath());
                    mediaDownloadPath.setIsAvailable(true);
                    mediaDownloadPath.setMetaData("");
                    mediaDownloadPath = metadataStatusRepository.save(mediaDownloadPath);
                }
            }
            media.setDownloadPath(mediaDownloadPath);
        }catch (Exception e){
            // Log error and set download path to null if metadata handling fails
            System.err.println("Error handling metadata status: " + e.getMessage());
            media.setDownloadPath(null);
        }}

    }
}