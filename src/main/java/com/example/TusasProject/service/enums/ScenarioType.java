package com.example.TusasProject.service.enums;

public enum ScenarioType {
    GROWTH("growth"),
    COLLAPSE("collapse"),
    DISCIPLINE("discipline"),
    TRANSFORMATIVE("transformative");

    private final String value;

    ScenarioType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}