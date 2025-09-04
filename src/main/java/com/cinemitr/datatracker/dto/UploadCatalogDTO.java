package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadCatalogDTO {
    private Long id;
    
    @JsonProperty("source_link")
    private String sourceLink;
    
    @JsonProperty("source_data")
    private String sourceData;
    
    private String status;
    
    @JsonProperty("media_type")
    private String mediaType;
    
    @JsonProperty("media_name")
    private String mediaName;
    
    @JsonProperty("media_names_list")
    private java.util.List<String> mediaNamesList;
    
    @JsonProperty("media_data")
    private String mediaData;
    
    private String metadata;

    // Constructors
    public UploadCatalogDTO() {}

    public UploadCatalogDTO(String sourceLink, String sourceData, String status, String mediaType, String mediaName) {
        this.sourceLink = sourceLink;
        this.sourceData = sourceData;
        this.status = status;
        this.mediaType = mediaType;
        this.mediaName = mediaName;
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public java.util.List<String> getMediaNamesList() {
        return mediaNamesList;
    }

    public void setMediaNamesList(java.util.List<String> mediaNamesList) {
        this.mediaNamesList = mediaNamesList;
    }

    public String getMediaData() {
        return mediaData;
    }

    public void setMediaData(String mediaData) {
        this.mediaData = mediaData;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}