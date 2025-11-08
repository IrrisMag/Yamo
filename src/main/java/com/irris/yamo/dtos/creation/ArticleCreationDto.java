package com.irris.yamo.dtos.creation;

import com.irris.yamo.entities.enums.BillingMode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleCreationDto {
    private String name;
    private String description;
    private Long categoryId;

    private int quantity;
    private Double estimatedWeight;

    private String material;
    private String color;
    private String size;

    private BillingMode billingMode;
    private List<Long> servicesIds;

}
