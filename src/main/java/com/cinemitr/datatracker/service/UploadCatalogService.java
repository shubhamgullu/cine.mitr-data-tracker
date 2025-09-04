package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.UploadCatalogDTO;
import com.cinemitr.datatracker.entity.ContentCatalog;
import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.entity.MetadataStatus;
import com.cinemitr.datatracker.entity.UploadCatalog;
import com.cinemitr.datatracker.enums.PathCategory;
import com.cinemitr.datatracker.repository.ContentCatalogRepository;
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
    
    @Autowired
    private ContentCatalogRepository contentRepository;

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
        
        // Set required fields with proper null handling - allow empty strings
        upload.setStatus(uploadDTO.getStatus() != null ? uploadDTO.getStatus() : "pending");
        upload.setMediaFormat(uploadDTO.getMediaData()); // Allow null/empty media format
        upload.setMetadata(uploadDTO.getMetadata()); // Allow null/empty metadata
        
        // Handle source link - check if it matches content table or create new content entry
        String sourceLink = uploadDTO.getSourceLink();
        if (sourceLink != null && !sourceLink.trim().isEmpty()) {
            // Try to find matching content by link
            ContentCatalog matchingContent = contentRepository.findByLink(sourceLink.trim());
            if (matchingContent != null) {
                upload.setSourceLink(matchingContent); // Map to existing content
            } else {
                // Create new content entry for the new link
                ContentCatalog newContent = createContentFromUpload(sourceLink.trim(), uploadDTO);
                upload.setSourceLink(newContent); // Map to newly created content
            }
        } else {
            upload.setSourceLink(null); // No link provided, create as new item without content mapping
        }
        
        // Handle source data - ensure it's never null due to NOT NULL constraint, allow empty strings
        String sourceDataValue = uploadDTO.getSourceData();
        if (sourceDataValue == null) {
            sourceDataValue = ""; // Allow empty string instead of default message
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
        upload.setStatus(dto.getStatus() != null ? dto.getStatus() : "pending");
        upload.setMediaFormat(dto.getMediaData()); // Allow null/empty media format
        upload.setMetadata(dto.getMetadata()); // Allow null/empty metadata
        
        // Handle source link - check if it matches content table or create new content entry
        String sourceLink = dto.getSourceLink();
        if (sourceLink != null && !sourceLink.trim().isEmpty()) {
            // Try to find matching content by link
            ContentCatalog matchingContent = contentRepository.findByLink(sourceLink.trim());
            if (matchingContent != null) {
                upload.setSourceLink(matchingContent); // Map to existing content
            } else {
                // Create new content entry for the new link
                ContentCatalog newContent = createContentFromUpload(sourceLink.trim(), dto);
                upload.setSourceLink(newContent); // Map to newly created content
            }
        } else {
            upload.setSourceLink(null); // No link provided, keep as new item without content mapping
        }
        
        // Handle source data - ensure it's never null due to NOT NULL constraint, allow empty strings
        String sourceDataValue = dto.getSourceData();
        if (sourceDataValue == null) {
            sourceDataValue = ""; // Allow empty string instead of default message
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
    
    private ContentCatalog createContentFromUpload(String link, UploadCatalogDTO uploadDTO) {
        try {
            ContentCatalog content = new ContentCatalog();
            content.setLink(link);
            
            // Set default values for auto-created content from upload
            content.setStatus("pending");
            content.setPriority("medium");
            content.setLocalStatus("not-available");
            content.setLocalFilePath(null);
            
            // Handle media from upload DTO
            Set<MediaCatalog> mediaSet = new HashSet<>();
            if (uploadDTO.getMediaName() != null && !uploadDTO.getMediaName().trim().isEmpty()) {
                String[] mediaNames = uploadDTO.getMediaName().split(",");
                
                Set<String> uniqueMediaNames = new LinkedHashSet<>();
                for (String mediaName : mediaNames) {
                    String trimmedMediaName = mediaName.trim();
                    if (!trimmedMediaName.isEmpty()) {
                        uniqueMediaNames.add(trimmedMediaName);
                    }
                }
                
                for (String uniqueMediaName : uniqueMediaNames) {
                    String mediaType = uploadDTO.getMediaType() != null && !uploadDTO.getMediaType().trim().isEmpty() 
                        ? uploadDTO.getMediaType() : "Movie";
                    MediaCatalog media = findOrCreateMediaByName(uniqueMediaName, mediaType);
                    mediaSet.add(media);
                }
            }
            content.setMediaList(mediaSet);
            
            // Save the content
            return contentRepository.save(content);
        } catch (Exception e) {
            System.err.println("Failed to create content from upload: " + e.getMessage());
            throw new RuntimeException("Failed to create content entry for link: " + link, e);
        }
    }
}