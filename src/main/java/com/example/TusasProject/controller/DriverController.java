package com.example.TusasProject.controller;

import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.dto.DriverRatingListDTO;
import com.example.TusasProject.dto.TrendImpactDTO;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverRepository driverRepository;
    private final DriverService driverService;

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @GetMapping("/by-trend")
    public List<DriverDTO> getDriversByTrend(@RequestParam String trendName) {
        return driverService.getDriversByTrend(trendName);
    }

    @GetMapping("/average-impacts")
    public List<TrendImpactDTO> getAverageImpactPerTrend() {
        return driverService.getAverageImpactPerTrend();
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateDrivers(@RequestBody List<DriverDTO> driverDtos) {
        driverService.updateDrivers(driverDtos);
        return ResponseEntity.ok("Updated");
    }

    @GetMapping("/definition")
    public ResponseEntity<String> getTrendDefinition(@RequestParam String trendName) {
        String definition = driverService.getTrendDefinition(trendName);
        if (definition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(definition);
    }

    @PostMapping("/submit-driver-ratings")
    public String submitRatings(@ModelAttribute DriverRatingListDTO dto) {
        driverService.submitDriverRatings(dto);
        return "redirect:/panel";
    }
}