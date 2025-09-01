package com.cinemitr.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_catalog")
@EntityListeners(AuditingEntityListener.class)
public class ContentCatalog {
    
    @Id
    @Column(name = "link", nullable = false, length = 500)
    private String link;
    
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "media_catalog_type", nullable = false)
    private MediaType mediaCatalogType;
    
    @Column(name = "media_catalog_name", nullable = false, columnDefinition = "TEXT")
    private String mediaCatalogName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.MEDIUM;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "local_status")
    private LocalStatus localStatus;
    
    @Column(name = "location_path", columnDefinition = "TEXT")
    private String locationPath;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "like_states")
    private LikeState likeStates;
    
    @Column(name = "comment_states", columnDefinition = "TEXT")
    private String commentStates;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_content_status")
    private UploadContentStatus uploadContentStatus;
    
    @Column(name = "linked_upload_catalog_link", length = 500)
    private String linkedUploadCatalogLink;
    
    // Enums
    public enum MediaType {
        MOVIE, ALBUM, WEB_SERIES, DOCUMENTARY
    }
    
    public enum ContentStatus {
        NEW, DOWNLOADED, ERROR, IN_PROGRESS
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH
    }
    
    public enum LikeState {
        LIKED, DISLIKED, NEUTRAL
    }
    
    public enum UploadContentStatus {
        PENDING_UPLOAD, UPLOADING, UPLOADED, UPLOAD_FAILED
    }
    
    public enum LocalStatus {
        AVAILABLE, NOT_AVAILABLE, PARTIALLY_AVAILABLE, DOWNLOADING, PROCESSING, CORRUPTED
    }
    
    // Constructors
    public ContentCatalog() {}
    
    // Audit field getters and setters
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
    
    public ContentCatalog(String link, MediaType mediaCatalogType, String mediaCatalogName, ContentStatus status) {
        this.link = link;
        this.mediaCatalogType = mediaCatalogType;
        this.mediaCatalogName = mediaCatalogName;
        this.status = status;
    }
    
    // Getters and Setters
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    // For backward compatibility - getId() now returns link since link is the primary key
    public String getId() {
        return link;
    }
    
    // For backward compatibility - setId() now sets link since link is the primary key  
    public void setId(String id) {
        this.link = id;
    }
    
    public MediaType getMediaCatalogType() {
        return mediaCatalogType;
    }
    
    public void setMediaCatalogType(MediaType mediaCatalogType) {
        this.mediaCatalogType = mediaCatalogType;
    }
    
    public String getMediaCatalogName() {
        return mediaCatalogName;
    }
    
    public void setMediaCatalogName(String mediaCatalogName) {
        this.mediaCatalogName = mediaCatalogName;
    }
    
    public ContentStatus getStatus() {
        return status;
    }
    
    public void setStatus(ContentStatus status) {
        this.status = status;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public LikeState getLikeStates() {
        return likeStates;
    }
    
    public void setLikeStates(LikeState likeStates) {
        this.likeStates = likeStates;
    }
    
    public String getCommentStates() {
        return commentStates;
    }
    
    public void setCommentStates(String commentStates) {
        this.commentStates = commentStates;
    }
    
    public UploadContentStatus getUploadContentStatus() {
        return uploadContentStatus;
    }
    
    public void setUploadContentStatus(UploadContentStatus uploadContentStatus) {
        this.uploadContentStatus = uploadContentStatus;
    }
    
    public String getLinkedUploadCatalogLink() {
        return linkedUploadCatalogLink;
    }
    
    public void setLinkedUploadCatalogLink(String linkedUploadCatalogLink) {
        this.linkedUploadCatalogLink = linkedUploadCatalogLink;
    }
    
    public LocalStatus getLocalStatus() {
        return localStatus;
    }
    
    public void setLocalStatus(LocalStatus localStatus) {
        this.localStatus = localStatus;
    }
    
    public String getLocationPath() {
        return locationPath;
    }
    
    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }
    
    // Helper methods for multiple media catalog names
    public String[] getMediaCatalogNameArray() {
        if (mediaCatalogName == null || mediaCatalogName.trim().isEmpty()) {
            return new String[0];
        }
        return mediaCatalogName.split(",");
    }
    
    public void setMediaCatalogNameArray(String[] names) {
        if (names == null || names.length == 0) {
            this.mediaCatalogName = "";
            return;
        }
        
        // Clean and join the names
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            String name = names[i].trim();
            if (!name.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(name);
            }
        }
        this.mediaCatalogName = sb.toString();
    }
    
    public void addMediaCatalogName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        
        String cleanName = name.trim();
        if (mediaCatalogName == null || mediaCatalogName.trim().isEmpty()) {
            this.mediaCatalogName = cleanName;
        } else {
            // Check if name already exists
            String[] existing = getMediaCatalogNameArray();
            for (String existingName : existing) {
                if (existingName.trim().equalsIgnoreCase(cleanName)) {
                    return; // Already exists, don't add duplicate
                }
            }
            this.mediaCatalogName += "," + cleanName;
        }
    }
    
    public boolean hasMediaCatalogName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String[] names = getMediaCatalogNameArray();
        String cleanName = name.trim();
        
        for (String existingName : names) {
            if (existingName.trim().equalsIgnoreCase(cleanName)) {
                return true;
            }
        }
        return false;
    }
}