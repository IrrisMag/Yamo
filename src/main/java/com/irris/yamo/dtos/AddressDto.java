package com.irris.yamo.dtos;

import com.irris.yamo.entities.enums.AddressType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private AddressType type;
    private String description;
}
