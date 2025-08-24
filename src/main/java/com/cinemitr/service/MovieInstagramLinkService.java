package com.cinemitr.service;

import com.cinemitr.model.MovieInstagramLink;
import com.cinemitr.repository.MovieInstagramLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieInstagramLinkService {

    @Autowired
    private MovieInstagramLinkRepository instagramLinkRepository;

    public List<MovieInstagramLink> getAllLinks() {
        return instagramLinkRepository.findAll();
    }

    public List<MovieInstagramLink> getActiveLinks() {
        return instagramLinkRepository.findActiveLinksOrderByCreatedAt();
    }

    public Optional<MovieInstagramLink> getLinkById(Long id) {
        return instagramLinkRepository.findById(id);
    }

    public MovieInstagramLink saveLink(MovieInstagramLink link) {
        return instagramLinkRepository.save(link);
    }

    public MovieInstagramLink createLink(String movieName, String category, String instagramLink, 
                                       String description) {
        MovieInstagramLink link = new MovieInstagramLink();
        link.setMovieName(movieName);
        link.setCategory(category);
        link.setInstagramLink(instagramLink);
        link.setDescription(description);
        link.setStatus(MovieInstagramLink.Status.ACTIVE);
        
        return instagramLinkRepository.save(link);
    }

    public MovieInstagramLink updateLink(MovieInstagramLink link) {
        return instagramLinkRepository.save(link);
    }

    public void deleteLink(Long id) {
        instagramLinkRepository.deleteById(id);
    }

    public void deactivateLink(Long id) {
        Optional<MovieInstagramLink> linkOpt = instagramLinkRepository.findById(id);
        if (linkOpt.isPresent()) {
            MovieInstagramLink link = linkOpt.get();
            link.setStatus(MovieInstagramLink.Status.INACTIVE);
            instagramLinkRepository.save(link);
        }
    }

    public List<MovieInstagramLink> searchLinksByMovieName(String movieName) {
        return instagramLinkRepository.findByMovieNameContainingIgnoreCase(movieName);
    }

    public List<MovieInstagramLink> getLinksByCategory(String category) {
        return instagramLinkRepository.findByCategoryIgnoreCase(category);
    }


    public List<String> getAllCategories() {
        return instagramLinkRepository.findAllCategories();
    }

    public List<MovieInstagramLink> getMostViewedLinks() {
        return instagramLinkRepository.findMostViewedLinks();
    }


    public void incrementViewCount(Long id) {
        Optional<MovieInstagramLink> linkOpt = instagramLinkRepository.findById(id);
        if (linkOpt.isPresent()) {
            MovieInstagramLink link = linkOpt.get();
            link.setViewCount(link.getViewCount() + 1);
            instagramLinkRepository.save(link);
        }
    }

    public void incrementClickCount(Long id) {
        Optional<MovieInstagramLink> linkOpt = instagramLinkRepository.findById(id);
        if (linkOpt.isPresent()) {
            MovieInstagramLink link = linkOpt.get();
            link.setClickCount(link.getClickCount() + 1);
            instagramLinkRepository.save(link);
        }
    }

    public List<MovieInstagramLink> getLinksByStatus(MovieInstagramLink.Status status) {
        return instagramLinkRepository.findByStatus(status);
    }
}