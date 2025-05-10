package com.example.TusasProject.service;

import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.TrendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TrendService {


    private final DriverRepository driverRepository;
    private final TrendRepository trendRepository;

    @Autowired
    public TrendService(DriverRepository driverRepository, TrendRepository trendRepository) {
        this.driverRepository = driverRepository;
        this.trendRepository = trendRepository;
    }

    public List<Driver> getDriversByTrend(String trend) {
        return driverRepository.findByTrend_TrendName(trend);
    }
    public List<Trend> getAllTrends() {
        return trendRepository.findAll();
    }

    public List<Trend> searchByName(String name) {
        return trendRepository.findByTrendNameContainingIgnoreCase(name);
    }
}