package com.cinemitr.controller;

import com.cinemitr.model.StatesCatalog;
import com.cinemitr.repository.StatesCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/states-catalog")
public class StatesCatalogController {
    
    @Autowired
    private StatesCatalogRepository statesCatalogRepository;
    
    @GetMapping
    public List<StatesCatalog> getAllStatesCatalogs() {
        return statesCatalogRepository.findAllOrderByCreatedOnDesc();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StatesCatalog> getStatesCatalogById(@PathVariable Long id) {
        Optional<StatesCatalog> statesCatalog = statesCatalogRepository.findById(id);
        return statesCatalog.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public StatesCatalog createStatesCatalog(@RequestBody StatesCatalog statesCatalog) {
        return statesCatalogRepository.save(statesCatalog);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StatesCatalog> updateStatesCatalog(@PathVariable Long id, @RequestBody StatesCatalog statesCatalogDetails) {
        Optional<StatesCatalog> optionalStatesCatalog = statesCatalogRepository.findById(id);
        
        if (optionalStatesCatalog.isPresent()) {
            StatesCatalog statesCatalog = optionalStatesCatalog.get();
            statesCatalog.setViews(statesCatalogDetails.getViews());
            statesCatalog.setSubscribers(statesCatalogDetails.getSubscribers());
            statesCatalog.setInteractions(statesCatalogDetails.getInteractions());
            statesCatalog.setTotalContent(statesCatalogDetails.getTotalContent());
            statesCatalog.setReach(statesCatalogDetails.getReach());
            statesCatalog.setImpressions(statesCatalogDetails.getImpressions());
            statesCatalog.setProfileVisits(statesCatalogDetails.getProfileVisits());
            statesCatalog.setWebsiteClicks(statesCatalogDetails.getWebsiteClicks());
            statesCatalog.setEmailClicks(statesCatalogDetails.getEmailClicks());
            statesCatalog.setCallClicks(statesCatalogDetails.getCallClicks());
            statesCatalog.setFollowersGained(statesCatalogDetails.getFollowersGained());
            statesCatalog.setFollowersLost(statesCatalogDetails.getFollowersLost());
            statesCatalog.setReelsCount(statesCatalogDetails.getReelsCount());
            statesCatalog.setStoriesCount(statesCatalogDetails.getStoriesCount());
            statesCatalog.setAvgEngagementRate(statesCatalogDetails.getAvgEngagementRate());
            statesCatalog.setUpdatedBy("system");
            
            return ResponseEntity.ok(statesCatalogRepository.save(statesCatalog));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStatesCatalog(@PathVariable Long id) {
        Optional<StatesCatalog> statesCatalog = statesCatalogRepository.findById(id);
        
        if (statesCatalog.isPresent()) {
            statesCatalogRepository.delete(statesCatalog.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats/total-views")
    public ResponseEntity<Long> getTotalViews() {
        Long totalViews = statesCatalogRepository.getTotalViews().orElse(0L);
        return ResponseEntity.ok(totalViews);
    }
    
    @GetMapping("/stats/total-subscribers")
    public ResponseEntity<Long> getTotalSubscribers() {
        Long totalSubscribers = statesCatalogRepository.getTotalSubscribers().orElse(0L);
        return ResponseEntity.ok(totalSubscribers);
    }
    
    @GetMapping("/stats/total-interactions")
    public ResponseEntity<Long> getTotalInteractions() {
        Long totalInteractions = statesCatalogRepository.getTotalInteractions().orElse(0L);
        return ResponseEntity.ok(totalInteractions);
    }
    
    @GetMapping("/stats/average-engagement-rate")
    public ResponseEntity<BigDecimal> getAverageEngagementRate() {
        BigDecimal avgEngagementRate = statesCatalogRepository.getAverageEngagementRate().orElse(BigDecimal.ZERO);
        return ResponseEntity.ok(avgEngagementRate);
    }
    
    @GetMapping("/min-engagement/{minRate}")
    public List<StatesCatalog> getStatesCatalogsByMinEngagement(@PathVariable BigDecimal minRate) {
        return statesCatalogRepository.findByMinEngagementRate(minRate);
    }
}