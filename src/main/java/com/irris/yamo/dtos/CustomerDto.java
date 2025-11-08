package com.irris.yamo.dtos;

import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.enums.CustomerSegment;
import com.irris.yamo.entities.enums.CustomerType;
import com.irris.yamo.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    private String profileImageUrl;
    private Gender gender;

    private CustomerType customerType; // INDIVIDUAL, ENTERPRISE, etc.


    private String companyName; // Pour les entreprises


    private String nui; // Numéro Unique d'Identification


    private String rccm; // Registre du Commerce

    private CustomerSegment customerSegment; // STANDARD, REGULAR, VIP, INACTIVE

    private Set<String> tags;


    private List<OrderDto> orders;


    private Double customerCredit;
    private Double customerDebt;
    private Double customerBalance;

}
