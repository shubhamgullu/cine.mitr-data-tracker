package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadCatalogDTO {
    private Long id;
    
    @JsonProperty("source_link")
    private String sourceLink;
    
    @JsonProperty("source_data")
    private String sourceData;
    
    private String status;
    
    @JsonProperty("media_id")
    private String mediaId;

    // Constructors
    public UploadCatalogDTO() {}

    public UploadCatalogDTO(String sourceLink, String sourceData, String status, String mediaId) {
        this.sourceLink = sourceLink;
        this.sourceData = sourceData;
        this.status = status;
        this.mediaId = mediaId;
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

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}