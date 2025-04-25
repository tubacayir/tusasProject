package com.example.TusasProject.dto;

public class TrendImpactDTO {
    private String trend;

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

    private double averageImpact;

    public TrendImpactDTO(String trend, double averageImpact) {
        this.trend = trend;
        this.averageImpact = averageImpact;
    }

    // Getters and setters
}
