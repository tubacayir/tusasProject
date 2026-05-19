package com.example.TusasProject.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "trends", uniqueConstraints = @UniqueConstraint(columnNames = "trend_name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trend_name", length = 1000)
    private String trendName;

    @Column(name = "avg_impact")
    private Float avgImpact;

    @Column(name = "avg_uncertainty")
    private Float avgUncertainty;

    @Column(name = "definition")
    private String definition;

    @ManyToOne
    @JoinColumn(name = "mega_trend_id")
    private MegaTrends megaTrend;

}

