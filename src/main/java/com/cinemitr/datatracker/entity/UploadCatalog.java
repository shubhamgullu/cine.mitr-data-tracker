package com.cinemitr.datatracker.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    
    @Column(name = "media_format")
    private String mediaFormat;
    
    @Column(name = "metadata", length = 9000)
    private String metadata;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "upload_media_mapping",
        joinColumns = @JoinColumn(name = "upload_id"),
        inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private Set<MediaCatalog> mediaList = new HashSet<>();

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

    public String getMediaFormat() {
        return mediaFormat;
    }

    public void setMediaFormat(String mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Set<MediaCatalog> getMediaList() {
        return mediaList;
    }

    public void setMediaList(Set<MediaCatalog> mediaList) {
        this.mediaList = mediaList;
    }
    
    public void addMedia(MediaCatalog media) {
        this.mediaList.add(media);
    }
    
    public void removeMedia(MediaCatalog media) {
        this.mediaList.remove(media);
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