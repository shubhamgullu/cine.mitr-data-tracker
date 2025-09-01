package com.cinemitr.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "media_catalog", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"name", "language"}))
@EntityListeners(AuditingEntityListener.class)
public class MediaCatalog extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MediaType type;
    
    @Column(name = "platform")
    private String platform;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "download_status")
    private DownloadStatus downloadStatus = DownloadStatus.NOT_DOWNLOADED;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "fun_facts", columnDefinition = "TEXT")
    private String funFacts;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "main_genre")
    private String mainGenre;
    
    @Column(name = "sub_genres", columnDefinition = "TEXT")
    private String subGenres;
    
    @Column(name = "created_by")
    private String createdBy = "system";
    
    @Column(name = "updated_by")
    private String updatedBy = "system";
    
    @CreatedDate
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;
    
    @LastModifiedDate
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
    
    // Enums
    public enum MediaType {
        MOVIE, ALBUM, WEB_SERIES, DOCUMENTARY
    }
    
    public enum DownloadStatus {
        NOT_DOWNLOADED, DOWNLOADED, PARTIALLY_DOWNLOADED
    }
    
    // Constructors
    public MediaCatalog() {}
    
    public MediaCatalog(String name, MediaType type) {
        this.name = name;
        this.type = type;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public MediaType getType() {
        return type;
    }
    
    public void setType(MediaType type) {
        this.type = type;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }
    
    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFunFacts() {
        return funFacts;
    }
    
    public void setFunFacts(String funFacts) {
        this.funFacts = funFacts;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getMainGenre() {
        return mainGenre;
    }
    
    public void setMainGenre(String mainGenre) {
        this.mainGenre = mainGenre;
    }
    
    public String getSubGenres() {
        return subGenres;
    }
    
    public void setSubGenres(String subGenres) {
        this.subGenres = subGenres;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy != null ? createdBy : "system";
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy != null ? updatedBy : "system";
    }
    
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
    
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }
    
    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }
}