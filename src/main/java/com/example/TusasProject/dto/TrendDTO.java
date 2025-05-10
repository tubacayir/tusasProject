package com.example.TusasProject.dto;

public class TrendDTO {
    private String trendName;
    private String definition;
    private double avgImpact;
    private Long id;

    public TrendDTO(Long id, String trendName, String definition, double avgImpact) {
        this.trendName = trendName;
        this.definition = definition;
        this.avgImpact = avgImpact;
        this.id = id;
    }

    // Getter'lar
    public String getTrendName() {
        return trendName;
    }

    public String getDefinition() {
        return definition;
    }

    public double getAvgImpact() {
        return avgImpact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}