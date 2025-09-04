package com.cinemitr.datatracker.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "content_catalog")
public class ContentCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link", nullable = false)
    private String link;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "content_media_mapping",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private Set<MediaCatalog> mediaList = new HashSet<>();

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "local_status", nullable = false)
    private String localStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_file_path")
    private MetadataStatus localFilePath;

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
    public ContentCatalog() {}

    public ContentCatalog(String link, String status, String priority, String localStatus) {
        this.link = link;
        this.status = status;
        this.priority = priority;
        this.localStatus = localStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLocalStatus() {
        return localStatus;
    }

    public void setLocalStatus(String localStatus) {
        this.localStatus = localStatus;
    }

    public MetadataStatus getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(MetadataStatus localFilePath) {
        this.localFilePath = localFilePath;
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