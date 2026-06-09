package com.example.TusasProject.service;

import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.service.enums.ScenarioType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DriverSelectionService {

    private static final int DRIVER_LIMIT = 2;

    private final DriverRepository driverRepository;

    public Map<ScenarioType, List<Driver>> selectDriversForScenarioTypes(Long trendId) {
        List<Driver> allDrivers = driverRepository.findByTrendId(trendId)
                .stream()
                .sorted(Comparator.comparingDouble(this::averageScore).reversed())
                .toList();

        Set<Long> usedDriverIds = new HashSet<>();
        Map<ScenarioType, List<Driver>> result = new EnumMap<>(ScenarioType.class);

        result.put(ScenarioType.GROWTH, selectGrowthDrivers(allDrivers, usedDriverIds));
        result.put(ScenarioType.COLLAPSE, selectCollapseDrivers(allDrivers, usedDriverIds));
        result.put(ScenarioType.DISCIPLINE, selectDisciplineDrivers(allDrivers, usedDriverIds));
        result.put(ScenarioType.TRANSFORMATIVE, selectTransformativeDrivers(allDrivers, usedDriverIds));

        return result;
    }

    private List<Driver> selectGrowthDrivers(List<Driver> drivers, Set<Long> usedIds) {
        return drivers.stream()
                .filter(driver -> isNotUsed(driver, usedIds))
                .limit(DRIVER_LIMIT)
                .peek(driver -> usedIds.add(driver.getId()))
                .toList();
    }

    private List<Driver> selectCollapseDrivers(List<Driver> drivers, Set<Long> usedIds) {
        return drivers.stream()
                .filter(driver -> isNotUsed(driver, usedIds))
                .filter(driver -> driver.getPolarity() != null && driver.getPolarity() == -1)
                .sorted(Comparator.comparingDouble(this::averageScore))
                .limit(DRIVER_LIMIT)
                .peek(driver -> usedIds.add(driver.getId()))
                .toList();
    }

    private List<Driver> selectDisciplineDrivers(List<Driver> drivers, Set<Long> usedIds) {
        return drivers.stream()
                .filter(driver -> isNotUsed(driver, usedIds))
                .filter(driver -> impact(driver) >= 3.0f && uncertainty(driver) <= 2.0f)
                .sorted(Comparator.comparingDouble(this::disciplineScore).reversed())
                .limit(DRIVER_LIMIT)
                .peek(driver -> usedIds.add(driver.getId()))
                .toList();
    }

    private List<Driver> selectTransformativeDrivers(List<Driver> drivers, Set<Long> usedIds) {
        return drivers.stream()
                .filter(driver -> isNotUsed(driver, usedIds))
                .filter(driver -> impact(driver) <= 2.0f && uncertainty(driver) >= 3.0f)
                .sorted(Comparator.comparingDouble(this::transformativeScore).reversed())
                .limit(DRIVER_LIMIT)
                .peek(driver -> usedIds.add(driver.getId()))
                .toList();
    }

    private boolean isNotUsed(Driver driver, Set<Long> usedIds) {
        return !usedIds.contains(driver.getId());
    }

    private double averageScore(Driver driver) {
        return (impact(driver) + uncertainty(driver)) / 2.0;
    }

    private double disciplineScore(Driver driver) {
        return impact(driver) - uncertainty(driver);
    }

    private double transformativeScore(Driver driver) {
        return uncertainty(driver) - impact(driver);
    }

    private float impact(Driver driver) {
        return driver.getImpact() != null ? driver.getImpact() : 0f;
    }

    private float uncertainty(Driver driver) {
        return driver.getUncertainty() != null ? driver.getUncertainty() : 0f;
    }
}