package com.irris.yamo.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticTaskDto {
    private Long id;
    private Long driverId;
    private Long relatedOrderId;

    private String taskType;
    private String status;

    private String ContactName;
    private String contactPhone;

    private LocalDate scheduledDate;
    private LocalTime timeFrom;
    private LocalTime timeTo;


    private AddressDto addressDto;

    private boolean hasArticlesToBeWeighed;
    private List<ArticleWeightingDto> articlesToBeWeighed;

    // ========== Ajoutés pour éviter endpoints GET dédiés ==========
    private String signaturePath;
    private List<String> photoPaths;
    private Integer sequenceOrder;  // Ordre dans la tournée optimisée

}
