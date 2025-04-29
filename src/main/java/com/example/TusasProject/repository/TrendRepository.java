package com.example.TusasProject.repository;

import com.example.TusasProject.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface TrendRepository extends JpaRepository<Trend, Integer> {
    @Query("SELECT trend FROM Trend trend WHERE trend.trend_name = :trendName")
    @Transactional(readOnly = true)
    List<Trend> findByTrendName(@org.springframework.data.repository.query.Param("trendName") String trendName);}


