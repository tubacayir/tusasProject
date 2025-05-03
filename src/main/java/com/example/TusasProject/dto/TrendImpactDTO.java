package com.example.TusasProject.dto;

public class TrendImpactDTO {
    private String trend;
    private double averageImpact;
    private String definition;

    public TrendImpactDTO(String trend, double averageImpact, String definition) {
        this.trend = trend;
        this.averageImpact = averageImpact;
        this.definition = definition;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public double getAverageImpact() {
        return averageImpact;
    }

    public void setAverageImpact(double averageImpact) {
        this.averageImpact = averageImpact;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }


    // Getters and setters
}
