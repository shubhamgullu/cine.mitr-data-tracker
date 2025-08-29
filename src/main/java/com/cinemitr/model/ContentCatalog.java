package com.cinemitr.model;

import javax.persistence.*;

@Entity
@Table(name = "content_catalog",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_content_catalog_link", columnNames = {"link"})
       })
public class ContentCatalog extends BaseEntity {
    
    @Column(name = "link", nullable = false, unique = true)
    private String link;
    
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
    
    @Column(name = "linked_upload_catalog_id")
    private Long linkedUploadCatalogId;
    
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
    
    public Long getLinkedUploadCatalogId() {
        return linkedUploadCatalogId;
    }
    
    public void setLinkedUploadCatalogId(Long linkedUploadCatalogId) {
        this.linkedUploadCatalogId = linkedUploadCatalogId;
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