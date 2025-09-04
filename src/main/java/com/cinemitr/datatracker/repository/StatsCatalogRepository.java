package com.cinemitr.datatracker.repository;

import com.cinemitr.datatracker.entity.StatsCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StatsCatalogRepository extends JpaRepository<StatsCatalog, Long> {
    List<StatsCatalog> findByPage(String page);
    List<StatsCatalog> findByDateBetween(Date startDate, Date endDate);
    List<StatsCatalog> findByDate(Date date);
}