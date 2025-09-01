package com.cinemitr.datatracker.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "upload_catalog")
public class UploadCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_link_id")
    private ContentCatalog sourceLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_data", nullable = false)
    private MetadataStatus sourceData;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id")
    private MediaCatalog media;

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
    public UploadCatalog() {}

    public UploadCatalog(String status) {
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentCatalog getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(ContentCatalog sourceLink) {
        this.sourceLink = sourceLink;
    }

    public MetadataStatus getSourceData() {
        return sourceData;
    }

    public void setSourceData(MetadataStatus sourceData) {
        this.sourceData = sourceData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MediaCatalog getMedia() {
        return media;
    }

    public void setMedia(MediaCatalog media) {
        this.media = media;
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