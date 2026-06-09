package com.example.TusasProject.service;

import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.dto.DriverRatingDTO;
import com.example.TusasProject.dto.DriverRatingListDTO;
import com.example.TusasProject.dto.TrendImpactDTO;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Movement;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.enums.DriverCategory;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.MovementRepository;
import com.example.TusasProject.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final TrendRepository trendRepository;
    private final MovementRepository movementRepository;

    public List<DriverDTO> getDriversByTrend(String trendName) {

        Trend trend = trendRepository.findByTrendNameIgnoreCase(trendName)
                .stream()
                .findFirst()
                .orElse(null);

        if (trend == null) {
            return List.of();
        }

        return driverRepository.findAll().stream()
                .filter(driver -> driver.getTrend().getId().equals(trend.getId()))
                .limit(30)
                .map(this::createDriverDto)
                .collect(Collectors.toList());
    }

    public List<TrendImpactDTO> getAverageImpactPerTrend() {

        List<Trend> trends = trendRepository.findAll()
                .stream()
                .limit(80)
                .toList();

        Map<Long, List<Driver>> driversByTrend = createDriverTrendMap();

        return trends.stream()
                .map(trend -> buildTrendImpactDto(trend, driversByTrend))
                .collect(Collectors.toList());
    }

    public void updateDrivers(List<DriverDTO> driverDtos) {

        for (DriverDTO dto : driverDtos) {
            Driver driver = driverRepository.findById(dto.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Driver not found: " + dto.getId()));

            driver.setImpact((float) dto.getImpact());
            driver.setUncertainty((float) dto.getUncertainty());

            driverRepository.save(driver);
        }
    }

    public String getTrendDefinition(String trendName) {

        return trendRepository.findByTrendNameIgnoreCase(trendName)
                .stream()
                .findFirst()
                .map(Trend::getDefinition)
                .orElse(null);
    }

    public void submitDriverRatings(DriverRatingListDTO driverRatingListDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer userId = userDetails.getId();

        for (DriverRatingDTO dto : driverRatingListDTO.getRatings()) {

            boolean emptyImpact = dto.getImpact() == null || dto.getImpact() == 0;
            boolean emptyUncertainty = dto.getUncertainty() == null || dto.getUncertainty() == 0;
            if (emptyImpact && emptyUncertainty) {
                continue;
            }
            Movement movement = createMovement(dto, userId);
            movementRepository.save(movement);
        }
    }

    private Movement createMovement(DriverRatingDTO dto, Integer userId) {
        Movement movement = new Movement();

        movement.setUserId(userId);
        movement.setDriverId(dto.getDriverId());
        movement.setImpact(dto.getImpact());
        movement.setUncertainty(dto.getUncertainty());
        return movement;
    }

    private DriverDTO createDriverDto(Driver driver) {

        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setTrend(driver.getTrend().getTrendName());
        dto.setDriver(driver.getDriverName());
        dto.setImpact(driver.getImpact() != null
                ? driver.getImpact()
                : 0.0f);
        dto.setUncertainty(driver.getUncertainty() != null
                ? driver.getUncertainty()
                : 0.0f
        );
        return dto;
    }

    private Map<Long, List<Driver>> createDriverTrendMap() {

        return driverRepository.findAll()
                .stream()
                .filter(driver -> driver.getTrend() != null)
                .collect(Collectors.groupingBy(
                        driver -> driver.getTrend().getId()
                ));
    }

    private TrendImpactDTO buildTrendImpactDto(Trend trend, Map<Long, List<Driver>> driversByTrend) {

        List<Driver> drivers = driversByTrend.getOrDefault(
                trend.getId(),
                Collections.emptyList()
        );

        return new TrendImpactDTO(
                trend.getTrendName(),
                trend.getDefinition(),
                averageImpactForCategory(drivers, DriverCategory.SOCIAL),
                averageImpactForCategory(drivers, DriverCategory.ECONOMIC),
                averageImpactForCategory(drivers, DriverCategory.ENVIRONMENTAL),
                averageImpactForCategory(drivers, DriverCategory.TECHNOLOGICAL),
                averageImpactForCategory(drivers, DriverCategory.POLITICAL)
        );
    }

    private double averageImpactForCategory(List<Driver> drivers, DriverCategory category) {

        return drivers.stream()
                .filter(driver ->
                        category.equals(driver.getDriverCategory()))
                .filter(driver ->
                        driver.getImpact() != null)
                .mapToDouble(Driver::getImpact)
                .average()
                .orElse(0.0);
    }
}