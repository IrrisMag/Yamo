package com.irris.yamo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends UserYamo{

    @Column(name = "cni_number")
    private String cniNumber; // Numéro CNI

    @Column(unique = true, name = "license_number")
    private String licenseNumber; // Permis de conduire

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_brand")
    private String vehicleBrand;

    @Column(name = "vehicle_color")
    private String vehicleColor;

    @Column(unique = true, name = "vehicle_number")
    private String vehicleNumber; // Matricule

    @Column(name = "vehicle_chassis_number")
    private String vehicleChassisNumber;

    @Column(name = "driver_comment")
    private String driverComment;

    // Statistiques DRIVER
    @Column(name = "total_deliveries")
    private Integer totalDeliveries = 0;

    @Column(name = "total_pickups")
    private Integer totalPickups = 0;

    @Column(name = "driver_average_rating")
    private Double driverAverageRating = 0.0;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "last_task_completion_time")
    private LocalDateTime lastTaskCompletionTime;


    // Adresse de base DRIVER (pressing)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_location_id")
    private Adresse baseLocation;
}
