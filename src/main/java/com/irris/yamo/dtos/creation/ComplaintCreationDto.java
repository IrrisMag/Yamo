package com.irris.yamo.dtos.creation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintCreationDto {
    private Long customerId;
    private Long orderId;
    private String type;
    private String description;
}
