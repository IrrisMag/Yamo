package com.irris.yamo.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long orderId;
    private String orderReference;
    private String type;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Long assignedToId;
    private String assignedToName;
    private String resolutionNotes;
}
