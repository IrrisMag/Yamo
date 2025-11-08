package com.irris.yamo.dtos.creation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleCategoryCreationDto {
    private String name;
    private String description;
    private String iconUrl;
}
