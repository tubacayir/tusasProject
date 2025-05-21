package com.example.TusasProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrendDTO {
    private String trendName;
    private String definition;
    private double avgImpact;
    private Long id;

    public TrendDTO(Long id, String trendName, String definition) {
        this.trendName = trendName;
        this.definition = definition;
        this.id = id;
    }


}