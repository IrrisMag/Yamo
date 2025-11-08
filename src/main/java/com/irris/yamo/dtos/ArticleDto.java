package com.irris.yamo.dtos;

import com.irris.yamo.dtos.creation.ArticleCreationDto;
import com.irris.yamo.entities.enums.BillingMode;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    private BillingMode billingMode;
    private Long categoryId;
    private ArticleCategoryDto category;  // ➕ Détails complets de la catégorie
    private List<Long> laundryServiceIds;

    private int quantity;
    private double estimatedWeight;
    private double actualWeight;

    private List<ArticleInstanceDto> instances;

    private boolean wasReceived;
    private boolean wasSorted;
    private boolean wasProcessed;
    private boolean wasPackaged;
}
