package com.irris.yamo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleWeightingDto {
    private Long articleId;
    private String name;
    private Double weight;
}
