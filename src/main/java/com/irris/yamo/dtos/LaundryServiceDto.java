package com.irris.yamo.dtos;

import com.irris.yamo.dtos.creation.ArticleCreationDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryServiceDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerKg;
    private BigDecimal pricePerItem;

    private List<ProcessStepDto> processSteps;

}
