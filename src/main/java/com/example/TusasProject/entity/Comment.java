package com.example.TusasProject.entity;
import com.example.TusasProject.dto.DriverRatingListDTO;
import com.example.TusasProject.entity.enums.DriverCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // Foreign key buradan tanımlanır
    private User user;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at", nullable = false, updatable = true)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

}


