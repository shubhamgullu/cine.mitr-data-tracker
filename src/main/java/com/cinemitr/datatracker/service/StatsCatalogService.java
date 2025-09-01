package com.cinemitr.datatracker.service;

import com.cinemitr.datatracker.dto.StatsCatalogDTO;
import com.cinemitr.datatracker.entity.StatsCatalog;
import com.cinemitr.datatracker.repository.StatsCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatsCatalogService {
    
    @Autowired
    private StatsCatalogRepository statsRepository;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<StatsCatalogDTO> getAllStats() {
        return statsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<StatsCatalogDTO> getStatsById(Long id) {
        return statsRepository.findById(id)
                .map(this::convertToDTO);
    }

    public StatsCatalogDTO saveStats(StatsCatalogDTO statsDTO) {
        StatsCatalog stats = convertToEntity(statsDTO);
        StatsCatalog savedStats = statsRepository.save(stats);
        return convertToDTO(savedStats);
    }

    public StatsCatalogDTO updateStats(Long id, StatsCatalogDTO statsDTO) {
        StatsCatalog stats = statsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stats not found with id: " + id));
        
        updateEntityFromDTO(stats, statsDTO);
        StatsCatalog updatedStats = statsRepository.save(stats);
        return convertToDTO(updatedStats);
    }

    public void deleteStats(Long id) {
        statsRepository.deleteById(id);
    }

    private StatsCatalogDTO convertToDTO(StatsCatalog stats) {
        StatsCatalogDTO dto = new StatsCatalogDTO();
        dto.setId(stats.getId());
        dto.setDate(dateFormat.format(stats.getDate()));
        dto.setTotalViews(String.format("%.0f", stats.getTotalViews()));
        dto.setSubscribers(String.format("%.0f", stats.getSubscribers()));
        dto.setInteraction(String.format("%.0f", stats.getInteraction()));
        dto.setContent(stats.getContent() != null ? stats.getContent().getLink() : "Daily analytics");
        dto.setPage(stats.getPage().toLowerCase().replace("CINE.MITR", "cine.mitr"));
        return dto;
    }

    private StatsCatalog convertToEntity(StatsCatalogDTO dto) {
        StatsCatalog stats = new StatsCatalog();
        updateEntityFromDTO(stats, dto);
        return stats;
    }

    private void updateEntityFromDTO(StatsCatalog stats, StatsCatalogDTO dto) {
        try {
            stats.setDate(dateFormat.parse(dto.getDate()));
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + dto.getDate(), e);
        }
        stats.setTotalViews(Double.parseDouble(dto.getTotalViews()));
        stats.setSubscribers(Double.parseDouble(dto.getSubscribers()));
        stats.setInteraction(Double.parseDouble(dto.getInteraction()));
        stats.setPage(dto.getPage().toUpperCase());
    }
}