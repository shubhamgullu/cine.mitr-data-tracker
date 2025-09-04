package com.cinemitr.datatracker.controller;

import com.cinemitr.datatracker.dto.StatsCatalogDTO;
import com.cinemitr.datatracker.service.StatsCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/states")
@CrossOrigin(origins = "*")
public class StatsCatalogController {
    
    @Autowired
    private StatsCatalogService statsService;

    @GetMapping
    public ResponseEntity<List<StatsCatalogDTO>> getAllStats() {
        List<StatsCatalogDTO> stats = statsService.getAllStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatsCatalogDTO> getStatsById(@PathVariable Long id) {
        return statsService.getStatsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StatsCatalogDTO> createStats(@RequestBody StatsCatalogDTO statsDTO) {
        StatsCatalogDTO savedStats = statsService.saveStats(statsDTO);
        return ResponseEntity.ok(savedStats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatsCatalogDTO> updateStats(@PathVariable Long id, @RequestBody StatsCatalogDTO statsDTO) {
        try {
            StatsCatalogDTO updatedStats = statsService.updateStats(id, statsDTO);
            return ResponseEntity.ok(updatedStats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStats(@PathVariable Long id) {
        statsService.deleteStats(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getStatsCount() {
        List<StatsCatalogDTO> stats = statsService.getAllStats();
        return ResponseEntity.ok((long) stats.size());
    }
}