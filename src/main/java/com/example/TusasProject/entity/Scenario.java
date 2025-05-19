package com.example.TusasProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "scenarios")
@Getter
@Setter
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trend_id")
    private Trend trend;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    private String scenarioText;

    private Boolean isPublished;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Scenario() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
