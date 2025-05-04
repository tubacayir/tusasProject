package com.example.TusasProject.service;

import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.repository.DriverRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TrendService {


    private final DriverRepository driverRepository;

    @Autowired
    public TrendService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<String> getDriversByTrend(String trend) {
        return driverRepository.findByTrend_TrendName(trend)
                .stream()
                .map(Driver::getDriverName)
                .collect(Collectors.toList());
    }

}