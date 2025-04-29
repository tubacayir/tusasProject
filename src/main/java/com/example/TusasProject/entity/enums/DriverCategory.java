package com.example.TusasProject.entity.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum DriverCategory {
    ENVIRONMENTAL("Environmental"),
    TECHNOLOGICAL("Technological"),
    SOCIAL("Social"),
    ECONOMIC("Economic"),
    POLITICAL("Political");

    private final String displayName;
    @Enumerated(EnumType.STRING)
    private DriverCategory category;
    DriverCategory(String displayName) {
        this.displayName = displayName;
    }


    public static DriverCategory fromString(String value) {
        for (DriverCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(value) || category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
