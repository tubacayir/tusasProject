package com.example.TusasProject.service;

import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.service.enums.ScenarioType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScenarioGenerationService {

    private final DriverSelectionService driverSelectionService;
    private final PromptBuilderService promptBuilderService;
    private final FlaskScenarioClient flaskScenarioClient;

    public Map<String, String> generateScenarios(Trend trend) {
        Map<ScenarioType, List<Driver>> selectedDrivers =
                driverSelectionService.selectDriversForScenarioTypes(trend.getId());

        Map<String, String> generatedScenarios = new LinkedHashMap<>();

        for (ScenarioType type : ScenarioType.values()) {
            List<Driver> drivers = selectedDrivers.get(type);

            if (drivers == null || drivers.isEmpty()) {
                generatedScenarios.put(type.value(), "Yeterli sayıda uygun driver bulunamadı.");
                continue;
            }

            String prompt = promptBuilderService.buildPrompt(type, trend, drivers);
            String scenarioText = flaskScenarioClient.generateScenario(prompt);

            generatedScenarios.put(type.value(), scenarioText);
        }

        return generatedScenarios;
    }
}