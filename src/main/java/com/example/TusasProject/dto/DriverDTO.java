package com.example.TusasProject.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DriverDTO {
    private String trend;
    private String driver;
    private double impact;
    private double uncertainty;

    private Long id;


}