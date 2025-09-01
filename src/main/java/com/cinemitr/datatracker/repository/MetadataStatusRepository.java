package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.MetadataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataStatusRepository extends JpaRepository<MetadataStatus, Long> {
    List<MetadataStatus> findByPathCategory(String pathCategory);
    List<MetadataStatus> findByIsAvailable(Boolean isAvailable);
}