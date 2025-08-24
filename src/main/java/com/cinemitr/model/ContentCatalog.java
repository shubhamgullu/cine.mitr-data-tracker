package com.cinemitr.model;

import javax.persistence.*;

@Entity
@Table(name = "content_catalog")
public class ContentCatalog extends BaseEntity {
    
    @Column(name = "link", nullable = false)
    private String link;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "media_catalog_type", nullable = false)
    private MediaType mediaCatalogType;
    
    @Column(name = "media_catalog_name", nullable = false)
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
    @Column(name = "like_states")
    private LikeState likeStates;
    
    @Column(name = "comment_states", columnDefinition = "TEXT")
    private String commentStates;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_content_status")
    private UploadContentStatus uploadContentStatus;
    
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
}