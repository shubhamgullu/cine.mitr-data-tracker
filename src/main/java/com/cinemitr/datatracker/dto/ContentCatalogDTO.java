package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentCatalogDTO {
    private Long id;
    private String link;
    
    @JsonProperty("media_type")
    private String mediaType;
    
    @JsonProperty("media_name")
    private String mediaName;
    
    @JsonProperty("media_names_list")
    private java.util.List<String> mediaNamesList;
    
    private String status;
    private String priority;
    
    @JsonProperty("local_status")
    private String localStatus;
    
    @JsonProperty("local_file_path")
    private String localFilePath;

    // Constructors
    public ContentCatalogDTO() {}

    public ContentCatalogDTO(String link, String mediaType, String mediaName, String status, 
                            String priority, String localStatus, String localFilePath) {
        this.link = link;
        this.mediaType = mediaType;
        this.mediaName = mediaName;
        this.status = status;
        this.priority = priority;
        this.localStatus = localStatus;
        this.localFilePath = localFilePath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLocalStatus() {
        return localStatus;
    }

    public void setLocalStatus(String localStatus) {
        this.localStatus = localStatus;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }
}