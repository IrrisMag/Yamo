package com.irris.yamo.mapper;

import com.irris.yamo.dtos.DriverDto;
import com.irris.yamo.entities.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public DriverDto toDto(Driver driver) {
        if (driver == null) {
            return null;
        }

        return DriverDto.builder()
                .id(driver.getId())
                .name(driver.getFullName())
                .email(driver.getEmail())
                .phoneNumber(driver.getPhoneNumber())
                .cniNumber(driver.getCniNumber())
                .licenseNumber(driver.getLicenseNumber())
                .vehicleType(driver.getVehicleType())
                .vehicleBrand(driver.getVehicleBrand())
                .vehicleColor(driver.getVehicleColor())
                .vehicleNumber(driver.getVehicleNumber())
                .vehicleChassisNumber(driver.getVehicleChassisNumber())
                .driverComment(driver.getDriverComment())
                .totalDeliveries(driver.getTotalDeliveries())
                .totalPickups(driver.getTotalPickups())
                .build();
    }
}
