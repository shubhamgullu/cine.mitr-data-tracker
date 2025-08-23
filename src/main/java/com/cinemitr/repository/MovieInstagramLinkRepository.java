package com.cinemitr.repository;

import com.cinemitr.model.MovieInstagramLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieInstagramLinkRepository extends JpaRepository<MovieInstagramLink, Long> {
    
    List<MovieInstagramLink> findByMovieNameContainingIgnoreCase(String movieName);
    
    List<MovieInstagramLink> findByCategoryIgnoreCase(String category);
    
    List<MovieInstagramLink> findByStatus(MovieInstagramLink.Status status);
    
    
    @Query("SELECT DISTINCT m.category FROM MovieInstagramLink m ORDER BY m.category")
    List<String> findAllCategories();
    
    @Query("SELECT m FROM MovieInstagramLink m WHERE m.status = 'ACTIVE' ORDER BY m.createdAt DESC")
    List<MovieInstagramLink> findActiveLinksOrderByCreatedAt();
    
    @Query("SELECT m FROM MovieInstagramLink m WHERE m.status = 'ACTIVE' ORDER BY m.viewCount DESC")
    List<MovieInstagramLink> findMostViewedLinks();
    
}