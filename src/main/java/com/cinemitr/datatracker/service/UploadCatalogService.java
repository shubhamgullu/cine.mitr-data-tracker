package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.UploadCatalogDTO;
import com.cinemitr.datatracker.entity.UploadCatalog;
import com.cinemitr.datatracker.repository.UploadCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UploadCatalogService {
    
    @Autowired
    private UploadCatalogRepository uploadRepository;

    public List<UploadCatalogDTO> getAllUploads() {
        return uploadRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UploadCatalogDTO> getUploadById(Long id) {
        return uploadRepository.findById(id)
                .map(this::convertToDTO);
    }

    public UploadCatalogDTO saveUpload(UploadCatalogDTO uploadDTO) {
        UploadCatalog upload = convertToEntity(uploadDTO);
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
        dto.setMediaData(upload.getMedia() != null ? upload.getMedia().getMediaName() + " - " + upload.getMedia().getMediaType() : "");
        return dto;
    }

    private UploadCatalog convertToEntity(UploadCatalogDTO dto) {
        UploadCatalog upload = new UploadCatalog();
        updateEntityFromDTO(upload, dto);
        return upload;
    }

    private void updateEntityFromDTO(UploadCatalog upload, UploadCatalogDTO dto) {
        upload.setStatus(dto.getStatus());
    }
}