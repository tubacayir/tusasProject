package com.example.TusasProject.controller;


import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.dto.DriverRatingDTO;
import com.example.TusasProject.dto.DriverRatingListDTO;
import com.example.TusasProject.dto.TrendImpactDTO;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Movement;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.MovementRepository;
import com.example.TusasProject.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {
    @Autowired
    private final DriverRepository driverRepository;
    private final TrendRepository trendRepository;
    private final MovementRepository movementRepository;

    // Tüm driverları getir
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // Belirli bir trend'e ait driverları getir
    @GetMapping("/by-trend")
    public List<DriverDTO> getDriversByTrend(@RequestParam String trendName) {
        List<Trend> trends = trendRepository.findByTrendNameIgnoreCase(trendName);

        if (!trends.isEmpty()) {
            Trend trend = trends.get(0);
            return driverRepository.findAll().stream()
                    .filter(driver -> driver.getTrend().getId().equals(trend.getId()))
                    .limit(30) // 30 driver ile sınırla
                    .map(driver -> {
                        DriverDTO dto = new DriverDTO();
                        dto.setId(driver.getId()); // önemli: frontend güncelleme için lazım
                        dto.setTrend(trend.getTrendName());
                        dto.setDriver(driver.getDriverName());
                        dto.setImpact(driver.getImpact() != null ? driver.getImpact() : (float) 0.0);
                        dto.setUncertainty(driver.getUncertainty() != null ? driver.getUncertainty() : (float) 0.0);
                        return dto;
                    }).collect(Collectors.toList());
        }
        return List.of();
    }


    // Her trend için ortalama impact hesapla
    @GetMapping("/average-impacts")
    public List<TrendImpactDTO> getAverageImpactPerTrend() {
        List<Trend> trends = trendRepository.findAll()
                .stream()
                .limit(80)
                .collect(Collectors.toList());

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

                    return new TrendImpactDTO(trend.getTrendName(), avgImpact, trend.getDefinition());
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateDrivers(@RequestBody List<DriverDTO> driverDtos) {
        for (DriverDTO dto : driverDtos) {
            Driver driver = driverRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found: " + dto.getId()));
            driver.setImpact((float) dto.getImpact());
            driver.setUncertainty((float) dto.getUncertainty());
            driverRepository.save(driver);
        }
        return ResponseEntity.ok("Updated");
    }

    @GetMapping("/definition")
    public ResponseEntity<String> getTrendDefinition(@RequestParam String trendName) {
        return trendRepository.findByTrendNameIgnoreCase(trendName).stream()
                .findFirst()
                .map(trend -> ResponseEntity.ok(trend.getDefinition()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/drivers/submit-driver-ratings")
    public String submitDriverRatings(@ModelAttribute DriverRatingListDTO driverRatingList, Model model) {
        for (DriverRatingDTO dto : driverRatingList.getRatings()) {
            Movement movement = new Movement();
            movement.setDriverId(dto.getDriverId().intValue());
            movement.setImpact(dto.getImpact());
            movement.setUncertainty(dto.getUncertainty());
            movement.setUserId(0);
            movementRepository.save(movement);
        }
        model.addAttribute("message", "Driver ratings submitted successfully!");
        return "redirect:/panel";
    }
}


