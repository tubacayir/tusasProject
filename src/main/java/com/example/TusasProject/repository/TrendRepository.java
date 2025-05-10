package com.example.TusasProject.repository;

import com.example.TusasProject.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {  // ID tipi Long olmalı
    @Query("SELECT trend FROM Trend trend WHERE LOWER(trend.trendName) = LOWER(:trendName)")
    @Transactional(readOnly = true)
    List<Trend> findByTrendNameIgnoreCase(@Param("trendName") String trendName);
    List<Trend> findByTrendNameContainingIgnoreCase(String trendName);

}
