package com.cinemitr.repository;

import com.cinemitr.model.StatesCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatesCatalogRepository extends JpaRepository<StatesCatalog, Long> {
    
    @Query("SELECT s FROM StatesCatalog s ORDER BY s.createdOn DESC")
    List<StatesCatalog> findAllOrderByCreatedOnDesc();
    
    @Query("SELECT SUM(s.views) FROM StatesCatalog s")
    Optional<Long> getTotalViews();
    
    @Query("SELECT SUM(s.subscribers) FROM StatesCatalog s")
    Optional<Long> getTotalSubscribers();
    
    @Query("SELECT SUM(s.interactions) FROM StatesCatalog s")
    Optional<Long> getTotalInteractions();
    
    @Query("SELECT AVG(s.avgEngagementRate) FROM StatesCatalog s")
    Optional<BigDecimal> getAverageEngagementRate();
    
    @Query("SELECT s FROM StatesCatalog s WHERE s.avgEngagementRate >= :minRate ORDER BY s.avgEngagementRate DESC")
    List<StatesCatalog> findByMinEngagementRate(BigDecimal minRate);
}