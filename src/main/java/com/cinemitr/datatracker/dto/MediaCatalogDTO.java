package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaCatalogDTO {
    private Long id;
    
    @JsonProperty("media_type")
    private String mediaType;
    
    @JsonProperty("media_name")
    private String mediaName;
    
    private String language;
    
    @JsonProperty("main_genres")
    private String mainGenres;
    
    @JsonProperty("sub_genres")
    private String subGenres;
    
    @JsonProperty("is_downloaded")
    private String isDownloaded; // Changed to String to match UI format
    
    @JsonProperty("download_path")
    private String downloadPath;
    
    @JsonProperty("available_on")
    private String availableOn;

    // Constructors
    public MediaCatalogDTO() {}

    public MediaCatalogDTO(String mediaType, String mediaName, String language, String mainGenres, 
                          String subGenres, String isDownloaded, String downloadPath, String availableOn) {
        this.mediaType = mediaType;
        this.mediaName = mediaName;
        this.language = language;
        this.mainGenres = mainGenres;
        this.subGenres = subGenres;
        this.isDownloaded = isDownloaded;
        this.downloadPath = downloadPath;
        this.availableOn = availableOn;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMainGenres() {
        return mainGenres;
    }

    public void setMainGenres(String mainGenres) {
        this.mainGenres = mainGenres;
    }

    public String getSubGenres() {
        return subGenres;
    }

    public void setSubGenres(String subGenres) {
        this.subGenres = subGenres;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getAvailableOn() {
        return availableOn;
    }

    public void setAvailableOn(String availableOn) {
        this.availableOn = availableOn;
    }
}