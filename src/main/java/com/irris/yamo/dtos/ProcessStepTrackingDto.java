package com.irris.yamo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessStepTrackingDto {
    private Long id;
    private Long instanceId;
    private Long processStepId;
    private boolean isCompleted;
    private String status;
    private String timestamp;
}
