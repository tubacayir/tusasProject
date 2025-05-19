package com.example.TusasProject.repository;

import com.example.TusasProject.entity.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    Scenario findByTrendId(Long trendId);
    List<Scenario> findByIsPublishedTrueOrderByUpdatedAtDesc();



}