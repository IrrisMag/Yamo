package com.irris.yamo.dtos.creation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverCreationDto {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String vehicleType;
    private String vehiclePlate;
    private String vehicleColor;
    private String cniNumber;

    private String vehicleBrand;
    private String vehicleChassisNumber;

    private String imageUrl;
}
