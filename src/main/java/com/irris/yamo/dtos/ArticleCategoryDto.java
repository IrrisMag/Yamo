package com.irris.yamo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleCategoryDto {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
}
