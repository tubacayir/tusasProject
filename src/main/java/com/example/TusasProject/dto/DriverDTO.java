package com.example.TusasProject.dto;

public class DriverDTO {
    private String trend;
    private String driver;
    private float impact;
    private float uncertainty;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public double getImpact() {
        return impact;
    }

    public void setImpact(float impact) {
        this.impact = impact;
    }

    public double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(float uncertainty) {
        this.uncertainty = uncertainty;
    }
    // Getter ve Setter’lar
}
