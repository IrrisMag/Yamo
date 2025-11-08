package com.irris.yamo.dtos.auth;

import com.irris.yamo.dtos.creation.CustomerCreationDto;
import com.irris.yamo.dtos.creation.DriverCreationDto;
import com.irris.yamo.entities.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {
    // Champs d'authentification (obligatoires)
    private String username;
    private String password;
    
    // Rôle de l'utilisateur
    private Role role; // ROLE_CUSTOMER, ROLE_DRIVER, ROLE_OPERATOR, ROLE_ADMIN
    
    // Données Customer (si role = CUSTOMER)
    private CustomerCreationDto customerData;
    
    // Données Driver (si role = DRIVER)
    private DriverCreationDto driverData;
    
    // Données communes pour Operator/Admin
    private String email;
    private String phoneNumber;
    private String name;
}
