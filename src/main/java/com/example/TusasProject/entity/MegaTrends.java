package com.example.TusasProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
