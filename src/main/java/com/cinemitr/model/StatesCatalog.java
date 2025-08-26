package com.cinemitr.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "states_catalog")
public class StatesCatalog extends BaseEntity {
    
    // Report Date (allows backdate entries)
    @Column(name = "report_date")
    private LocalDate reportDate;
    
    // Basic Metrics
    @Column(name = "views")
    private Integer views = 0;
    
    @Column(name = "subscribers")
    private Integer subscribers = 0;
    
    @Column(name = "interactions")
    private Integer interactions = 0;
    
    @Column(name = "total_content")
    private Integer totalContent = 0;
    
    // Reach & Engagement Metrics
    @Column(name = "reach")
    private Integer reach = 0;
    
    @Column(name = "impressions")
    private Integer impressions = 0;
    
    @Column(name = "profile_visits")
    private Integer profileVisits = 0;
    
    @Column(name = "website_clicks")
    private Integer websiteClicks = 0;
    
    // Contact Metrics
    @Column(name = "email_clicks")
    private Integer emailClicks = 0;
    
    @Column(name = "call_clicks")
    private Integer callClicks = 0;
    
    // Growth Metrics
    @Column(name = "followers_gained")
    private Integer followersGained = 0;
    
    @Column(name = "followers_lost")
    private Integer followersLost = 0;
    
    // Content-Specific Metrics
    @Column(name = "reels_count")
    private Integer reelsCount = 0;
    
    @Column(name = "stories_count")
    private Integer storiesCount = 0;
    
    @Column(name = "avg_engagement_rate", precision = 5, scale = 2)
    private BigDecimal avgEngagementRate = BigDecimal.ZERO;
    
    // Constructors
    public StatesCatalog() {}
    
    // Getters and Setters
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    public Integer getViews() {
        return views;
    }
    
    public void setViews(Integer views) {
        this.views = views != null ? views : 0;
    }
    
    public Integer getSubscribers() {
        return subscribers;
    }
    
    public void setSubscribers(Integer subscribers) {
        this.subscribers = subscribers != null ? subscribers : 0;
    }
    
    public Integer getInteractions() {
        return interactions;
    }
    
    public void setInteractions(Integer interactions) {
        this.interactions = interactions != null ? interactions : 0;
    }
    
    public Integer getTotalContent() {
        return totalContent;
    }
    
    public void setTotalContent(Integer totalContent) {
        this.totalContent = totalContent != null ? totalContent : 0;
    }
    
    public Integer getReach() {
        return reach;
    }
    
    public void setReach(Integer reach) {
        this.reach = reach != null ? reach : 0;
    }
    
    public Integer getImpressions() {
        return impressions;
    }
    
    public void setImpressions(Integer impressions) {
        this.impressions = impressions != null ? impressions : 0;
    }
    
    public Integer getProfileVisits() {
        return profileVisits;
    }
    
    public void setProfileVisits(Integer profileVisits) {
        this.profileVisits = profileVisits != null ? profileVisits : 0;
    }
    
    public Integer getWebsiteClicks() {
        return websiteClicks;
    }
    
    public void setWebsiteClicks(Integer websiteClicks) {
        this.websiteClicks = websiteClicks != null ? websiteClicks : 0;
    }
    
    public Integer getEmailClicks() {
        return emailClicks;
    }
    
    public void setEmailClicks(Integer emailClicks) {
        this.emailClicks = emailClicks != null ? emailClicks : 0;
    }
    
    public Integer getCallClicks() {
        return callClicks;
    }
    
    public void setCallClicks(Integer callClicks) {
        this.callClicks = callClicks != null ? callClicks : 0;
    }
    
    public Integer getFollowersGained() {
        return followersGained;
    }
    
    public void setFollowersGained(Integer followersGained) {
        this.followersGained = followersGained != null ? followersGained : 0;
    }
    
    public Integer getFollowersLost() {
        return followersLost;
    }
    
    public void setFollowersLost(Integer followersLost) {
        this.followersLost = followersLost != null ? followersLost : 0;
    }
    
    public Integer getReelsCount() {
        return reelsCount;
    }
    
    public void setReelsCount(Integer reelsCount) {
        this.reelsCount = reelsCount != null ? reelsCount : 0;
    }
    
    public Integer getStoriesCount() {
        return storiesCount;
    }
    
    public void setStoriesCount(Integer storiesCount) {
        this.storiesCount = storiesCount != null ? storiesCount : 0;
    }
    
    public BigDecimal getAvgEngagementRate() {
        return avgEngagementRate;
    }
    
    public void setAvgEngagementRate(BigDecimal avgEngagementRate) {
        this.avgEngagementRate = avgEngagementRate != null ? avgEngagementRate : BigDecimal.ZERO;
    }
}