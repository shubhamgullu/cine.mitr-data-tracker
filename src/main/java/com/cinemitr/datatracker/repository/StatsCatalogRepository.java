package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.StatesCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StatsCatalogRepository extends JpaRepository<StatesCatalog, Long> {
    List<StatesCatalog> findByPage(String page);
    List<StatesCatalog> findByDateBetween(Date startDate, Date endDate);
    List<StatesCatalog> findByDate(Date date);
}