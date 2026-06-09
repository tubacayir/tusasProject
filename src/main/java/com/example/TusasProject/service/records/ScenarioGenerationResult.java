package com.example.TusasProject.service.records;

import com.example.TusasProject.entity.Trend;

import java.util.Map;

public record ScenarioGenerationResult(Trend trend, Map<String, String> scenarios) { }