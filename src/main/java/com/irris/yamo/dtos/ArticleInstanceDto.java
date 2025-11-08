package com.irris.yamo.dtos;

import com.irris.yamo.dtos.creation.ArticleCreationDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleInstanceDto {
    private Long id;
    private Long articleId;
    private List<ProcessStepTrackingDto> processStepTrackings;
    private String code;
    private String status;
}
