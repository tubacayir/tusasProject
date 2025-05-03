package com.example.TusasProject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_trend_feedback")
@Data
public class UserTrendFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mega_trend_id")
    private MegaTrends megaTrend;

    @ManyToOne
    @JoinColumn(name = "trend_id")
    private Trend trend;

    @Column(name = "avg_impact")
    private Double impactRate;

    @Column(name = "avg_uncertainty")
    private Double uncertainty;
}
