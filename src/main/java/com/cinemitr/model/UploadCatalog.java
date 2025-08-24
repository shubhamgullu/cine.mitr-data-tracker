package com.cinemitr.model;

import javax.persistence.*;

@Entity
@Table(name = "upload_catalog")
public class UploadCatalog extends BaseEntity {
    
    @Column(name = "content_catalog_link", nullable = false)
    private String contentCatalogLink;
    
    @Column(name = "content_block")
    private String contentBlock;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "media_catalog_type", nullable = false)
    private MediaType mediaCatalogType;
    
    @Column(name = "media_catalog_name", nullable = false)
    private String mediaCatalogName;
    
    @Column(name = "content_catalog_location")
    private String contentCatalogLocation;
    
    @Column(name = "upload_catalog_location")
    private String uploadCatalogLocation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private UploadStatus uploadStatus;
    
    @Column(name = "upload_catalog_caption", columnDefinition = "TEXT")
    private String uploadCatalogCaption;
    
    // Enums
    public enum MediaType {
        MOVIE, ALBUM, WEB_SERIES, DOCUMENTARY
    }
    
    public enum UploadStatus {
        COMPLETED, IN_PROGRESS, UPLOADED
    }
    
    // Constructors
    public UploadCatalog() {}
    
    public UploadCatalog(String contentCatalogLink, MediaType mediaCatalogType, String mediaCatalogName, UploadStatus uploadStatus) {
        this.contentCatalogLink = contentCatalogLink;
        this.mediaCatalogType = mediaCatalogType;
        this.mediaCatalogName = mediaCatalogName;
        this.uploadStatus = uploadStatus;
    }
    
    // Getters and Setters
    public String getContentCatalogLink() {
        return contentCatalogLink;
    }
    
    public void setContentCatalogLink(String contentCatalogLink) {
        this.contentCatalogLink = contentCatalogLink;
    }
    
    public String getContentBlock() {
        return contentBlock;
    }
    
    public void setContentBlock(String contentBlock) {
        this.contentBlock = contentBlock;
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
    
    public String getContentCatalogLocation() {
        return contentCatalogLocation;
    }
    
    public void setContentCatalogLocation(String contentCatalogLocation) {
        this.contentCatalogLocation = contentCatalogLocation;
    }
    
    public String getUploadCatalogLocation() {
        return uploadCatalogLocation;
    }
    
    public void setUploadCatalogLocation(String uploadCatalogLocation) {
        this.uploadCatalogLocation = uploadCatalogLocation;
    }
    
    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }
    
    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
    
    public String getUploadCatalogCaption() {
        return uploadCatalogCaption;
    }
    
    public void setUploadCatalogCaption(String uploadCatalogCaption) {
        this.uploadCatalogCaption = uploadCatalogCaption;
    }
}