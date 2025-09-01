package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadCatalogDTO {
    private Long id;
    
    @JsonProperty("source_link")
    private String sourceLink;
    
    @JsonProperty("source_data")
    private String sourceData;
    
    private String status;
    
    @JsonProperty("media_data")
    private String mediaData;

    // Constructors
    public UploadCatalogDTO() {}

    public UploadCatalogDTO(String sourceLink, String sourceData, String status, String mediaData) {
        this.sourceLink = sourceLink;
        this.sourceData = sourceData;
        this.status = status;
        this.mediaData = mediaData;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMediaData() {
        return mediaData;
    }

    public void setMediaData(String mediaData) {
        this.mediaData = mediaData;
    }
}