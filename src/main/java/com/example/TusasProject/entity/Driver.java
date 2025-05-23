package com.example.TusasProject.entity;
import com.example.TusasProject.dto.DriverRatingListDTO;
import com.example.TusasProject.entity.enums.DriverCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends DriverRatingListDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "uncertainty")
    private Float uncertainty;

    @Column(name = "impact")
    private Float impact;

    @Column(name = "polarity")
    private Float polarity;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_category")
    private DriverCategory driverCategory;

    @ManyToOne
    @JoinColumn(name = "trend_id", referencedColumnName = "id")
    private Trend trend;

}

