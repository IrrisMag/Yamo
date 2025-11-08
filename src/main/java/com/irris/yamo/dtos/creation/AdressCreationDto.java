package com.irris.yamo.dtos.creation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdressCreationDto {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private String type;
    private String description;
}
