package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.ContentCatalogDTO;
import com.cinemitr.datatracker.dto.UploadCatalogDTO;
import com.cinemitr.datatracker.entity.ContentCatalog;
import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.entity.MetadataStatus;
import com.cinemitr.datatracker.entity.UploadCatalog;
import com.cinemitr.datatracker.enums.PathCategory;
import com.cinemitr.datatracker.repository.ContentCatalogRepository;
import com.cinemitr.datatracker.repository.MediaCatalogRepository;
import com.cinemitr.datatracker.repository.MetadataStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentCatalogService {
    
    @Autowired
    private ContentCatalogRepository contentRepository;
    
    @Autowired
    private MediaCatalogRepository mediaRepository;
    
    @Autowired
    private MetadataStatusRepository metadataStatusRepository;
    
    @Autowired
    private UploadCatalogService uploadService;
    
    private String toTitleCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder titleCase = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) continue;

            // Capitalize first letter if it's a letter
            titleCase.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));

            if (i < words.length - 1) {
                titleCase.append(" ");
            }
        }

        return titleCase.toString();
    }


    public List<ContentCatalogDTO> getAllContent() {
        return contentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ContentCatalogDTO> getContentById(Long id) {
        return contentRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ContentCatalogDTO saveContent(ContentCatalogDTO contentDTO) {
        ContentCatalog content = new ContentCatalog();
        content.setLink(contentDTO.getLink());
        content.setContentType(contentDTO.getContentType());
        content.setContentMetadata(contentDTO.getContentMetadata());
        content.setStatus(contentDTO.getStatus());
        if (contentDTO.getPriority()==null || contentDTO.getPriority().isEmpty())
            content.setPriority("low");
        else
            content.setPriority(contentDTO.getPriority());
        content.setLocalStatus(contentDTO.getLocalStatus());
        
        // Handle multiple media names
        Set<MediaCatalog> mediaSet = new HashSet<>();
        if (contentDTO.getMediaName() != null && !contentDTO.getMediaName().trim().isEmpty()) {
            String[] mediaNames = contentDTO.getMediaName().split(",");
            
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
                String camelCaseName = toTitleCase(uniqueMediaName);
                MediaCatalog media = findOrCreateMediaByName(camelCaseName, contentDTO.getMediaType());
                mediaSet.add(media);
            }
        }
        
        content.setMediaList(mediaSet);
        ContentCatalog savedContent = contentRepository.save(content);
        
        // Create corresponding upload entry with same details
        createUploadFromContent(savedContent, contentDTO);
        
        return convertToDTO(savedContent);
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
                // This handles the case where another thread created it between our checks
                existingMedia = mediaRepository.findByMediaNameAndMediaType(mediaName, actualMediaType);
                if (existingMedia != null) {
                    return existingMedia;
                }
                throw new RuntimeException("Failed to create or find media: " + mediaName + " of type: " + actualMediaType, e);
            }
        }
    }

    public ContentCatalogDTO updateContent(Long id, ContentCatalogDTO contentDTO) {
        ContentCatalog content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));
        
        updateEntityFromDTO(content, contentDTO);
        ContentCatalog updatedContent = contentRepository.save(content);
        return convertToDTO(updatedContent);
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }

    public ContentCatalogDTO convertToDTO(ContentCatalog content) {
        ContentCatalogDTO dto = new ContentCatalogDTO();
        dto.setId(content.getId());
        dto.setLink(content.getLink());
        dto.setContentType(content.getContentType());
        dto.setContentMetadata(content.getContentMetadata());
        
        // Handle multiple media
        if (content.getMediaList() != null && !content.getMediaList().isEmpty()) {
            List<String> mediaNames = content.getMediaList().stream()
                    .map(MediaCatalog::getMediaName)
                    .collect(Collectors.toList());
            
            dto.setMediaNamesList(mediaNames);
            dto.setMediaName(String.join(", ", mediaNames));
            
            // For media type, use the first media's type or default
            String mediaType = content.getMediaList().stream()
                    .findFirst()
                    .map(MediaCatalog::getMediaType)
                    .orElse("");
            dto.setMediaType(mediaType);
        } else {
            dto.setMediaType("");
            dto.setMediaName("");
            dto.setMediaNamesList(new ArrayList<>());
        }
        
        dto.setStatus(content.getStatus());
        dto.setPriority(content.getPriority());
        dto.setLocalStatus(content.getLocalStatus());
        dto.setLocalFilePath(content.getLocalFilePath() != null ? content.getLocalFilePath().getPath() : "");
        return dto;
    }

    private ContentCatalog convertToEntity(ContentCatalogDTO dto) {
        ContentCatalog content = new ContentCatalog();
        updateEntityFromDTO(content, dto);
        return content;
    }

    private void updateEntityFromDTO(ContentCatalog content, ContentCatalogDTO dto) {
        content.setLink(dto.getLink());
        content.setContentType(dto.getContentType());
        content.setContentMetadata(dto.getContentMetadata());
        content.setStatus(dto.getStatus());
        content.setPriority(dto.getPriority());
        content.setLocalStatus(dto.getLocalStatus());
        
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
                String camelCaseName = toTitleCase(uniqueMediaName);
                MediaCatalog media = findOrCreateMediaByName(camelCaseName, dto.getMediaType());
                mediaSet.add(media);
            }
        }
        
        content.setMediaList(mediaSet);
    }
    
    private void createUploadFromContent(ContentCatalog savedContent, ContentCatalogDTO contentDTO) {
        try {
            // Create upload entity directly since we need to set the ContentCatalog entity reference
            UploadCatalog upload = new UploadCatalog();
            upload.setSourceLink(savedContent); // Set the actual ContentCatalog entity
            upload.setStatus("new-content"); // Use 'New Content' status for newly added content
            upload.setMediaFormat(null); // Allow empty media format for auto-generated uploads
            upload.setMetadata("Auto-generated upload entry from content: " + savedContent.getLink());
            
            // Create MetadataStatus for source data
            MetadataStatus sourceDataMeta = new MetadataStatus();
            sourceDataMeta.setPath("");
            sourceDataMeta.setPathCategory(PathCategory.UPLOADED_FILE);
            sourceDataMeta.setMetaData("Auto-generated from content entry");
            sourceDataMeta.setIsAvailable(true);
            sourceDataMeta = metadataStatusRepository.save(sourceDataMeta);
            upload.setSourceData(sourceDataMeta);
            
            // Handle media list from content
            if (contentDTO.getMediaName() != null && !contentDTO.getMediaName().trim().isEmpty()) {
                Set<MediaCatalog> mediaSet = new HashSet<>();
                String[] mediaNames = contentDTO.getMediaName().split(",");
                
                Set<String> uniqueMediaNames = new LinkedHashSet<>();
                for (String mediaName : mediaNames) {
                    String trimmedMediaName = mediaName.trim();
                    if (!trimmedMediaName.isEmpty()) {
                        uniqueMediaNames.add(trimmedMediaName);
                    }
                }
                
                for (String uniqueMediaName : uniqueMediaNames) {
                    String camelCaseName = toTitleCase(uniqueMediaName);
                    MediaCatalog media = findOrCreateMediaByName(camelCaseName, contentDTO.getMediaType());
                    mediaSet.add(media);
                }
                upload.setMediaList(mediaSet);
            }
            
            // Save the upload entry directly
            uploadService.saveUploadEntity(upload);
        } catch (Exception e) {
            // Log the error but don't fail the content creation
            System.err.println("Failed to create upload entry from content: " + e.getMessage());
        }
    }
}