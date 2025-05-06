package com.example.TusasProject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "movment")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "impact", nullable = true)
    private Integer impact;

    @Column(name = "uncertainty", nullable = true)
    private Integer uncertainty;

    @Column(name = "driver_id", nullable = false)
    private Integer driverId;

    @Column(name = "created_at", nullable = false, updatable = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
