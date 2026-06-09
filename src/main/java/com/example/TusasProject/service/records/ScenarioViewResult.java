package com.example.TusasProject.service.records;

import com.example.TusasProject.entity.Trend;

import java.util.Map;

public record ScenarioViewResult(Trend trend, Map<String, String> scenarios) { }