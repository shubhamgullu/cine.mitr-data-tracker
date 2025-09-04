package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.UploadCatalogDTO;
import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.entity.MetadataStatus;
import com.cinemitr.datatracker.entity.UploadCatalog;
import com.cinemitr.datatracker.enums.PathCategory;
import com.cinemitr.datatracker.repository.MediaCatalogRepository;
import com.cinemitr.datatracker.repository.MetadataStatusRepository;
import com.cinemitr.datatracker.repository.UploadCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UploadCatalogService {
    
    @Autowired
    private UploadCatalogRepository uploadRepository;
    
    @Autowired
    private MediaCatalogRepository mediaRepository;
    
    @Autowired
    private MetadataStatusRepository metadataStatusRepository;

    public List<UploadCatalogDTO> getAllUploads() {
        return uploadRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UploadCatalogDTO> getUploadById(Long id) {
        return uploadRepository.findById(id)
                .map(this::convertToDTO);
    }

    public UploadCatalog saveUploadEntity(UploadCatalog upload) {
        return uploadRepository.save(upload);
    }

    public UploadCatalogDTO saveUpload(UploadCatalogDTO uploadDTO) {
        UploadCatalog upload = new UploadCatalog();
        
        // Set required fields with proper null handling
        upload.setStatus(uploadDTO.getStatus());
        upload.setMediaFormat(uploadDTO.getMediaData() != null ? uploadDTO.getMediaData() : "HD Video, 1080p, MP4");
        upload.setMetadata(uploadDTO.getMetadata());
        
        // Handle source data - ensure it's never null due to NOT NULL constraint
        String sourceDataValue = uploadDTO.getSourceData();
        if (sourceDataValue == null || sourceDataValue.trim().isEmpty()) {
            sourceDataValue = "No source data provided"; // Default value to avoid constraint violation
        }
        
        // Create MetadataStatus for source data
        MetadataStatus sourceDataMeta = new MetadataStatus();
        sourceDataMeta.setPath(""); // Set empty path
        sourceDataMeta.setPathCategory(PathCategory.UPLOADED_FILE);
        sourceDataMeta.setMetaData(sourceDataValue);
        sourceDataMeta.setIsAvailable(true);
        sourceDataMeta = metadataStatusRepository.save(sourceDataMeta);
        
        upload.setSourceData(sourceDataMeta);
        
        // Handle multiple media names similar to ContentCatalogService
        Set<MediaCatalog> mediaSet = new HashSet<>();
        if (uploadDTO.getMediaName() != null && !uploadDTO.getMediaName().trim().isEmpty()) {
            String[] mediaNames = uploadDTO.getMediaName().split(",");
            
            // Use LinkedHashSet to preserve order and eliminate duplicates from input
            Set<String> uniqueMediaNames = new LinkedHashSet<>();
            for (String mediaName : mediaNames) {
                String trimmedMediaName = mediaName.trim();
                if (!trimmedMediaName.isEmpty()) {
                    uniqueMediaNames.add(trimmedMediaName);
                }
            }
            
            // Process each unique media name
            for (String uniqueMediaName : uniqueMediaNames) {
                MediaCatalog media = findOrCreateMediaByName(uniqueMediaName, uploadDTO.getMediaType());
                mediaSet.add(media);
            }
        }
        
        upload.setMediaList(mediaSet);
        UploadCatalog savedUpload = uploadRepository.save(upload);
        return convertToDTO(savedUpload);
    }

    public UploadCatalogDTO updateUpload(Long id, UploadCatalogDTO uploadDTO) {
        UploadCatalog upload = uploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Upload not found with id: " + id));
        
        updateEntityFromDTO(upload, uploadDTO);
        UploadCatalog updatedUpload = uploadRepository.save(upload);
        return convertToDTO(updatedUpload);
    }

    public void deleteUpload(Long id) {
        uploadRepository.deleteById(id);
    }

    private UploadCatalogDTO convertToDTO(UploadCatalog upload) {
        UploadCatalogDTO dto = new UploadCatalogDTO();
        dto.setId(upload.getId());
        dto.setSourceLink(upload.getSourceLink() != null ? upload.getSourceLink().getLink() : "");
        dto.setSourceData(upload.getSourceData() != null ? upload.getSourceData().getMetaData() : "");
        dto.setStatus(upload.getStatus());
        dto.setMediaData(upload.getMediaFormat() != null ? upload.getMediaFormat() : "");
        dto.setMetadata(upload.getMetadata() != null ? upload.getMetadata() : "");
        
        // Handle multiple media similar to ContentCatalogService
        if (upload.getMediaList() != null && !upload.getMediaList().isEmpty()) {
            List<String> mediaNames = upload.getMediaList().stream()
                    .map(MediaCatalog::getMediaName)
                    .collect(Collectors.toList());
            
            dto.setMediaNamesList(mediaNames);
            dto.setMediaName(String.join(", ", mediaNames));
            
            // For media type, use the first media's type or default
            String mediaType = upload.getMediaList().stream()
                    .findFirst()
                    .map(MediaCatalog::getMediaType)
                    .orElse("");
            dto.setMediaType(mediaType);
        } else {
            dto.setMediaType("");
            dto.setMediaName("");
            dto.setMediaNamesList(new ArrayList<>());
        }
        
        return dto;
    }

    private UploadCatalog convertToEntity(UploadCatalogDTO dto) {
        UploadCatalog upload = new UploadCatalog();
        updateEntityFromDTO(upload, dto);
        return upload;
    }

    private void updateEntityFromDTO(UploadCatalog upload, UploadCatalogDTO dto) {
        upload.setStatus(dto.getStatus());
        upload.setMediaFormat(dto.getMediaData() != null ? dto.getMediaData() : "HD Video, 1080p, MP4");
        upload.setMetadata(dto.getMetadata());
        
        // Handle source data - ensure it's never null due to NOT NULL constraint
        String sourceDataValue = dto.getSourceData();
        if (sourceDataValue == null || sourceDataValue.trim().isEmpty()) {
            sourceDataValue = "No source data provided";
        }
        
        // Update or create MetadataStatus for source data
        MetadataStatus sourceDataMeta = upload.getSourceData();
        if (sourceDataMeta == null) {
            sourceDataMeta = new MetadataStatus();
            sourceDataMeta.setPath("");
            sourceDataMeta.setPathCategory(PathCategory.UPLOADED_FILE);
            sourceDataMeta.setIsAvailable(true);
        }
        sourceDataMeta.setMetaData(sourceDataValue);
        sourceDataMeta = metadataStatusRepository.save(sourceDataMeta);
        upload.setSourceData(sourceDataMeta);
        
        // Handle multiple media names
        Set<MediaCatalog> mediaSet = new HashSet<>();
        if (dto.getMediaName() != null && !dto.getMediaName().trim().isEmpty()) {
            String[] mediaNames = dto.getMediaName().split(",");
            
            // Use LinkedHashSet to preserve order and eliminate duplicates from input
            Set<String> uniqueMediaNames = new LinkedHashSet<>();
            for (String mediaName : mediaNames) {
                String trimmedMediaName = mediaName.trim();
                if (!trimmedMediaName.isEmpty()) {
                    uniqueMediaNames.add(trimmedMediaName);
                }
            }
            
            // Process each unique media name
            for (String uniqueMediaName : uniqueMediaNames) {
                MediaCatalog media = findOrCreateMediaByName(uniqueMediaName, dto.getMediaType());
                mediaSet.add(media);
            }
        }
        
        upload.setMediaList(mediaSet);
    }
    
    private MediaCatalog findOrCreateMediaByName(String mediaName, String mediaType) {
        String actualMediaType = mediaType != null && !mediaType.trim().isEmpty() ? mediaType : "Movie";
        
        // Search by both name AND type to respect unique constraint
        MediaCatalog existingMedia = mediaRepository.findByMediaNameAndMediaType(mediaName, actualMediaType);
        
        if (existingMedia != null) {
            return existingMedia;
        }
        
        // Use synchronized block to prevent race conditions when creating new media
        synchronized (this) {
            // Double-check after acquiring lock
            existingMedia = mediaRepository.findByMediaNameAndMediaType(mediaName, actualMediaType);
            if (existingMedia != null) {
                return existingMedia;
            }
            
            // Create new media with proper error handling
            try {
                MediaCatalog newMedia = new MediaCatalog();
                newMedia.setMediaName(mediaName);
                newMedia.setMediaType(actualMediaType);
                newMedia.setLanguage("English");
                newMedia.setMainGenres("Action");
                newMedia.setSubGenres("");
                newMedia.setIsDownloaded(false);
                newMedia.setAvailableOn("Unknown");
                
                return mediaRepository.save(newMedia);
            } catch (Exception e) {
                // If save fails due to constraint violation, try to find the media again
                existingMedia = mediaRepository.findByMediaNameAndMediaType(mediaName, actualMediaType);
                if (existingMedia != null) {
                    return existingMedia;
                }
                throw new RuntimeException("Failed to create or find media: " + mediaName + " of type: " + actualMediaType, e);
            }
        }
    }
}