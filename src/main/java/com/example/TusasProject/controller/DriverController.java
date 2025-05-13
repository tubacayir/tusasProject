package com.example.TusasProject.controller;


import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.dto.DriverRatingDTO;
import com.example.TusasProject.dto.DriverRatingListDTO;
import com.example.TusasProject.dto.TrendImpactDTO;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Movement;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.entity.enums.DriverCategory;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.MovementRepository;
import com.example.TusasProject.repository.TrendRepository;
import com.example.TusasProject.repository.UserRepository;
import com.example.TusasProject.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverRepository driverRepository;
    private final TrendRepository trendRepository;
    private final MovementRepository movementRepository;
    private final UserRepository userRepository;




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
                    .limit(30)
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
        List<Trend> trends = trendRepository.findAll().stream().limit(80).toList();
        List<Driver> allDrivers = driverRepository.findAll();
        Map<Long, List<Driver>> driversByTrend = allDrivers.stream()
                .filter(d -> d.getTrend() != null)
                .collect(Collectors.groupingBy(d -> d.getTrend().getId()));

        return trends.stream().map(trend -> {
            List<Driver> drivers = driversByTrend.getOrDefault(trend.getId(), Collections.emptyList());

            double avgSocialImpact = averageImpactForCategory(drivers, DriverCategory.SOCIAL);
            double avgEconomicImpact = averageImpactForCategory(drivers, DriverCategory.ECONOMIC);
            double avgEnvironmentalImpact = averageImpactForCategory(drivers, DriverCategory.ENVIRONMENTAL);
            double avgTechnologicalImpact = averageImpactForCategory(drivers, DriverCategory.TECHNOLOGICAL);
            double avgPoliticalImpact = averageImpactForCategory(drivers, DriverCategory.POLITICAL);

            return new TrendImpactDTO(
                    trend.getTrendName(),
                    trend.getDefinition(),
                    avgSocialImpact,
                    avgEconomicImpact,
                    avgEnvironmentalImpact,
                    avgTechnologicalImpact,
                    avgPoliticalImpact
            );
        }).collect(Collectors.toList());
    }

    private double averageImpactForCategory(List<Driver> drivers, DriverCategory category) {
        return drivers.stream()
                .filter(d -> category.equals(d.getDriverCategory()))
                .filter(d -> d.getImpact() != null)
                .mapToDouble(Driver::getImpact)
                .average()
                .orElse(0.0);
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


    @PostMapping("/submit-driver-ratings")
    public String submitRatings(@ModelAttribute DriverRatingListDTO driverRatingListDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer userId = userDetails.getId();

        for (DriverRatingDTO dto : driverRatingListDTO.getRatings()) {
            if ((dto.getImpact() == null || dto.getImpact() == 0) &&
                    (dto.getUncertainty() == null || dto.getUncertainty() == 0)) {
                continue;
            }

            Movement movement = new Movement();
            movement.setUserId(userId);
            movement.setDriverId(dto.getDriverId());
            movement.setImpact(dto.getImpact());
            movement.setUncertainty(dto.getUncertainty());

            movementRepository.save(movement);
        }

        return "redirect:/panel";
    }
    }


