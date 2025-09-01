package com.cinemitr.datatracker.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stats_catalog")
public class StatsCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "total_views", nullable = false)
    private Double totalViews;

    @Column(name = "subscribers", nullable = false)
    private Double subscribers;

    @Column(name = "interaction", nullable = false)
    private Double interaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ContentCatalog content;

    @Column(name = "page", nullable = false)
    private String page;

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
    public StatsCatalog() {}

    public StatsCatalog(Date date, Double totalViews, Double subscribers, Double interaction, String page) {
        this.date = date;
        this.totalViews = totalViews;
        this.subscribers = subscribers;
        this.interaction = interaction;
        this.page = page;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Double totalViews) {
        this.totalViews = totalViews;
    }

    public Double getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Double subscribers) {
        this.subscribers = subscribers;
    }

    public Double getInteraction() {
        return interaction;
    }

    public void setInteraction(Double interaction) {
        this.interaction = interaction;
    }

    public ContentCatalog getContent() {
        return content;
    }

    public void setContent(ContentCatalog content) {
        this.content = content;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
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