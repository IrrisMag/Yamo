package com.irris.yamo.dtos.creation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreationDto {
    private String name;
    private String email;
    private String phoneNumber;

    private String dateOfBirth;
    private String gender;

    private String customerType; // Individual or Company

    private String nui;
    private String rccm;

}
