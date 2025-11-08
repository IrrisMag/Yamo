package com.irris.yamo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessStepDto {
    private Long id;
    private String name;
    private String description;
}
