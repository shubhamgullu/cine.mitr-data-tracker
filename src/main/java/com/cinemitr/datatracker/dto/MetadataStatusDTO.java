package com.cinemitr.datatracker.dto;

import com.cinemitr.datatracker.enums.PathCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataStatusDTO {
    private Long id;
    
    @JsonProperty("path_category")
    private PathCategory pathCategory;
    
    private String path;
    
    @JsonProperty("is_available")
    private Boolean isAvailable;
    
    @JsonProperty("meta_data")
    private String metaData;

    public MetadataStatusDTO() {}

    public MetadataStatusDTO(PathCategory pathCategory, String path, Boolean isAvailable, String metaData) {
        this.pathCategory = pathCategory;
        this.path = path;
        this.isAvailable = isAvailable;
        this.metaData = metaData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PathCategory getPathCategory() {
        return pathCategory;
    }

    public void setPathCategory(PathCategory pathCategory) {
        this.pathCategory = pathCategory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}