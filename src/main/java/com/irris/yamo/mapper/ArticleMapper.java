package com.irris.yamo.mapper;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.LaundryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    private final ArticleInstanceMapper articleInstanceMapper;
    private final ArticleCategoryMapper articleCategoryMapper;

    public ArticleDto toDto(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleDto.builder()
                .id(article.getId())
                .name(article.getName())
                .description(article.getDescription())
                .price(article.getPrice())
                .billingMode(article.getBillingMode())
                .categoryId(article.getCategory() != null ? article.getCategory().getId() : null)
                .category(article.getCategory() != null ? articleCategoryMapper.toDto(article.getCategory()) : null)
                .laundryServiceIds(article.getLaundryServices() != null ?
                        article.getLaundryServices().stream()
                                .map(LaundryService::getId)
                                .collect(Collectors.toList()) : null)
                .quantity(article.getQuantity() != null ? article.getQuantity() : 0)
                .estimatedWeight(article.getEstimatedWeight() != null ? article.getEstimatedWeight().doubleValue() : 0.0)
                .actualWeight(article.getActualWeight() != null ? article.getActualWeight().doubleValue() : 0.0)
                .instances(article.getInstances() != null ?
                        article.getInstances().stream()
                                .map(articleInstanceMapper::toDto)
                                .collect(Collectors.toList()) : null)
                .wasReceived(article.isWasReceived())
                .wasSorted(article.isWasSorted())
                .wasProcessed(article.isWasProcessed())
                .wasPackaged(article.isWasPackaged())
                .build();
    }
}
