package com.example.TusasProject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mega_trend")
@Data
public class MegaTrends {

    @Id
    @Column(name = "mega_trend_id")
    private Long id;

    @Column(name = "mega_trend_name")
    private String name;
}
