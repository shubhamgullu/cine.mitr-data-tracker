package com.cinemitr.datatracker.entity;

import com.cinemitr.datatracker.enums.PathCategory;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "metadata_status")
public class MetadataStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "path_category", nullable = false)
    private PathCategory pathCategory;

    @Column(name = "path", length = 1024, nullable = false)
    private String path;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Lob
    @Column(name = "meta_data", nullable = false)
    private String metaData;

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
    public MetadataStatus() {}

    public MetadataStatus(PathCategory pathCategory, String path, Boolean isAvailable, String metaData) {
        this.pathCategory = pathCategory;
        this.path = path;
        this.isAvailable = isAvailable;
        this.metaData = metaData;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PathCategory getPathCategory() {
        return pathCategory;
    }

    public void setPathCategory(PathCategory pathCategory) {
        this.pathCategory = pathCategory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
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