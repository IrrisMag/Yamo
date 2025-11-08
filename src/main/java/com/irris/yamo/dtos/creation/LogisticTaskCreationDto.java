package com.irris.yamo.dtos.creation;

import com.irris.yamo.dtos.AddressDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticTaskCreationDto {

    private String taskType; // Pickup or Delivery

    private AddressDto existingAddress;
    private AdressCreationDto newAddress;

    private String ContactName;
    private String contactPhone;

    private LocalDate scheduledDate;
    private LocalTime timeFrom;
    private LocalTime timeTo;
}
