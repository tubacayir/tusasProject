package com.example.TusasProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioGenerationRequest {
    private Long trendId;

    private String collapseScenarioText;
    private String disciplineScenarioText;
    private String growthScenarioText;
    private String transformativeScenarioText;
}