package com.example.TusasProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrendImpactDTO {
    private String trend;
    private String definition;
    double avgSocialImpact;
    double avgEconomicImpact;
    double avgEnvironmentalImpact;
    double avgTechnologicalImpact;
    double avgPoliticalImpact;

    public TrendImpactDTO(String trend, String definition, double avgSocialImpact, double avgEconomicImpact,
                          double avgEnvironmentalImpact, double avgTechnologicalImpact,
                          double avgPoliticalImpact) {
        this.trend = trend;
        this.definition = definition;
        this.avgSocialImpact = avgSocialImpact;
        this.avgEconomicImpact=avgEconomicImpact;
        this.avgTechnologicalImpact=avgTechnologicalImpact;
        this.avgEnvironmentalImpact=avgEnvironmentalImpact;
        this.avgPoliticalImpact=avgPoliticalImpact;
    }


}
