package com.example.TusasProject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRatingDTO {
    private Long userId;
    private Integer driverId;
    private String name;
    private Integer impact;
    private Integer uncertainty;


}
