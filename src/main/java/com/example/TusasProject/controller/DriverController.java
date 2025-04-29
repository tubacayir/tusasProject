package com.example.TusasProject.controller;


import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.dto.TrendImpactDTO;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverRepository driverRepository;
    private final TrendRepository trendRepository;

    // Tüm driverları getir
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // Belirli bir trend'e ait driverları getir
    @GetMapping("/by-trend")
    public List<DriverDTO> getDriversByTrend(@RequestParam String trendName) {
        List<Trend> trends = trendRepository.findByTrendName(trendName);

        if (!trends.isEmpty()) {
            Trend trend = trends.get(0);
            return driverRepository.findAll().stream()
                    .filter(driver -> driver.getTrend().getId().equals(trend.getId()))
                    .map(driver -> {
                        DriverDTO dto = new DriverDTO();
                        dto.setTrend(trend.getTrend_name());
                        dto.setDriver(driver.getDriverName());
                        dto.setImpact(driver.getImpact() != null ? driver.getImpact() : 0.0);
                        dto.setUncertainty(driver.getUncertainty() != null ? driver.getUncertainty() : 0.0);
                        return dto;
                    }).collect(Collectors.toList());
        }
        return List.of();
    }


    // Her trend için ortalama impact hesapla
    @GetMapping("/average-impacts")
    public List<TrendImpactDTO> getAverageImpactPerTrend() {
        List<Trend> trends = trendRepository.findAll();

        return trends.stream()
                .map(trend -> {
                    List<Driver> drivers = driverRepository.findAll()
                            .stream()
                            .filter(driver -> driver.getTrend().getId().equals(trend.getId()))
                            .collect(Collectors.toList());

                    double avgImpact = drivers.stream()
                            .mapToDouble(d -> d.getImpact() != null ? d.getImpact() : 0.0)
                            .average()
                            .orElse(0.0);

                    return new TrendImpactDTO(trend.getTrend_name(), avgImpact);
                })
                .collect(Collectors.toList());
    }
}
