package com.irris.yamo.dtos.creation;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryServiceCreationDto {
    private String name;
    private String description;
    private Double pricePerKg;
    private Double pricePerItem;

    private List<Long> processStepIds;
}
