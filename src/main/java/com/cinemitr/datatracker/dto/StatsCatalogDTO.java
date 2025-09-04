package com.cinemitr.datatracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsCatalogDTO {
    private Long id;
    private String date;
    
    @JsonProperty("total_views")
    private String totalViews;
    
    private String subscribers;
    private String interaction;
    private String content;
    private String page;

    // Constructors
    public StatsCatalogDTO() {}

    public StatsCatalogDTO(String date, String totalViews, String subscribers, String interaction, 
                          String content, String page) {
        this.date = date;
        this.totalViews = totalViews;
        this.subscribers = subscribers;
        this.interaction = interaction;
        this.content = content;
        this.page = page;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}