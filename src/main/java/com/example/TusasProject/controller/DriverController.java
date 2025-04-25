package com.example.TusasProject.controller;

import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.dto.TrendImpactDTO;
import com.example.TusasProject.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private ExcelService excelService;

    @GetMapping
    public List<DriverDTO> getDrivers() throws IOException {
        return excelService.readExcelDrivers("/Users/tubacayir/IdeaProjects/TusasProject/src/main/resources/Drivers.xlsx");
    }
    @GetMapping("/average-impacts")
    public List<TrendImpactDTO> getAverageImpactPerTrend() throws IOException {
        List<DriverDTO> allDrivers = excelService.readExcelDrivers("/Users/tubacayir/IdeaProjects/TusasProject/src/main/resources/Drivers.xlsx");

        Map<String, List<Double>> trendImpacts = new HashMap<>();

        for (DriverDTO driver : allDrivers) {
            // We group all impacts by their trend (no domain filtering!)
            trendImpacts
                    .computeIfAbsent(driver.getTrend(), k -> new ArrayList<>())
                    .add(driver.getImpact());
        }

        List<TrendImpactDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : trendImpacts.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            result.add(new TrendImpactDTO(entry.getKey(), avg));
        }

        return result;
    }
    @GetMapping("/by-trend")
    public List<DriverDTO> getDriversByTrend(@RequestParam String trend) throws IOException {
        List<DriverDTO> allDrivers = excelService.readExcelDrivers("/Users/tubacayir/IdeaProjects/TusasProject/src/main/resources/Drivers.xlsx");
        List<DriverDTO> filtered = new ArrayList<>();

        for (DriverDTO dto : allDrivers) {
            if (dto.getTrend().equalsIgnoreCase(trend)) {
                filtered.add(dto);
            }
        }

        return filtered;
    }


}

