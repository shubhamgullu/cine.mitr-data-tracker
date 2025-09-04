package com.cinemitr.datatracker.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "media_catalog", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_media_name_type", columnNames = {"media_name", "media_type"})
       })
public class MediaCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "media_name", nullable = false)
    private String mediaName;

    @Column(name = "language")
    private String language;

    @Column(name = "is_downloaded", nullable = false)
    private Boolean isDownloaded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "download_path")
    private MetadataStatus downloadPath;

    @Column(name = "main_genres")
    private String mainGenres;

    @Column(name = "sub_genres")
    private String subGenres;

    @Column(name = "available_on")
    private String availableOn;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    // Constructors
    public MediaCatalog() {}

    public MediaCatalog(String mediaType, String mediaName, String language, Boolean isDownloaded, 
                       String mainGenres, String subGenres, String availableOn) {
        this.mediaType = mediaType;
        this.mediaName = mediaName;
        this.language = language;
        this.isDownloaded = isDownloaded;
        this.mainGenres = mainGenres;
        this.subGenres = subGenres;
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

    public Boolean getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(Boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public MetadataStatus getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(MetadataStatus downloadPath) {
        this.downloadPath = downloadPath;
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

    public String getAvailableOn() {
        return availableOn;
    }

    public void setAvailableOn(String availableOn) {
        this.availableOn = availableOn;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}