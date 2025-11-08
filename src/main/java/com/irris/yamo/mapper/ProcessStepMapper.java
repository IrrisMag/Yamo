package com.irris.yamo.mapper;

import com.irris.yamo.dtos.ProcessStepDto;
import com.irris.yamo.entities.ProcessStep;
import org.springframework.stereotype.Component;

@Component
public class ProcessStepMapper {

    public ProcessStepDto toDto(ProcessStep processStep) {
        if (processStep == null) {
            return null;
        }

        return ProcessStepDto.builder()
                .id(processStep.getId())
                .name(processStep.getName())
                .description(processStep.getDescription())
                .build();
    }
}
