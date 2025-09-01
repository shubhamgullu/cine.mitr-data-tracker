package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.ContentCatalogDTO;
import com.cinemitr.datatracker.entity.ContentCatalog;
import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.repository.ContentCatalogRepository;
import com.cinemitr.datatracker.repository.MediaCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContentCatalogService {
    
    @Autowired
    private ContentCatalogRepository contentRepository;
    
    @Autowired
    private MediaCatalogRepository mediaRepository;

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
        ContentCatalog content = convertToEntity(contentDTO);
        ContentCatalog savedContent = contentRepository.save(content);
        return convertToDTO(savedContent);
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

    private ContentCatalogDTO convertToDTO(ContentCatalog content) {
        ContentCatalogDTO dto = new ContentCatalogDTO();
        dto.setId(content.getId());
        dto.setLink(content.getLink());
        dto.setMediaType(content.getMedia() != null ? content.getMedia().getMediaType() : "");
        dto.setMediaName(content.getMedia() != null ? content.getMedia().getMediaName() : "");
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
        content.setStatus(dto.getStatus());
        content.setPriority(dto.getPriority());
        content.setLocalStatus(dto.getLocalStatus());
        
        // Find media by name if provided
        if (dto.getMediaName() != null && !dto.getMediaName().isEmpty()) {
            MediaCatalog media = mediaRepository.findByMediaName(dto.getMediaName());
            content.setMedia(media);
        }
    }
}