package com.irris.yamo.mapper;

import com.irris.yamo.dtos.ArticleInstanceDto;
import com.irris.yamo.dtos.ProcessStepTrackingDto;
import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.entities.ProcessStepTracking;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ArticleInstanceMapper {

    public ArticleInstanceDto toDto(ArticleInstance instance) {
        if (instance == null) {
            return null;
        }

        return ArticleInstanceDto.builder()
                .id(instance.getId())
                .articleId(instance.getArticle() != null ? instance.getArticle().getId() : null)
                .code(instance.getCode())
                .status(instance.getStatus() != null ? instance.getStatus().name() : null)
                .processStepTrackings(instance.getTrackings() != null ?
                        instance.getTrackings().stream()
                                .map(this::toTrackingDto)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    private ProcessStepTrackingDto toTrackingDto(ProcessStepTracking tracking) {
        if (tracking == null) {
            return null;
        }

        return ProcessStepTrackingDto.builder()
                .id(tracking.getId())
                .processStepId(tracking.getProcessStep() != null ? tracking.getProcessStep().getId() : null)
                .status(tracking.getStatus() != null ? tracking.getStatus().name() : null)
                .build();
    }
}
