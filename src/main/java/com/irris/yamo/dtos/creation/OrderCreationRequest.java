package com.irris.yamo.dtos.creation;

import com.irris.yamo.dtos.AddressDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreationRequest {
    private Long customerId;
    private List<ArticleCreationDto> articles;

    private LogisticTaskCreationDto pickupTask;
    private LogisticTaskCreationDto deliveryTask;




}
