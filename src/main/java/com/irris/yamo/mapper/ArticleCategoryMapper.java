package com.irris.yamo.mapper;

import com.irris.yamo.dtos.ArticleCategoryDto;
import com.irris.yamo.entities.ArticleCategory;
import org.springframework.stereotype.Component;

@Component
public class ArticleCategoryMapper {

    public ArticleCategoryDto toDto(ArticleCategory category) {
        if (category == null) {
            return null;
        }

        return ArticleCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .build();
    }

    public ArticleCategory toEntity(ArticleCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return ArticleCategory.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .iconUrl(dto.getIconUrl())
                .isActive(true)
                .build();
    }
}
