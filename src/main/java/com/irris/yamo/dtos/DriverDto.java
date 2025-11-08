package com.irris.yamo.dtos;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    private String cniNumber; // Numéro CNI


    private String licenseNumber; // Permis de conduire


    private String vehicleType;


    private String vehicleBrand;


    private String vehicleColor;


    private String vehicleNumber; // Matricule


    private String vehicleChassisNumber;


    private String driverComment;

    // Statistiques DRIVER

    private Integer totalDeliveries;
    private Integer totalPickups;
}
