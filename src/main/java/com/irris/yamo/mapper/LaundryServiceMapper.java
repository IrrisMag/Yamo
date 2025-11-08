package com.irris.yamo.mapper;

import com.irris.yamo.dtos.LaundryServiceDto;
import com.irris.yamo.dtos.ProcessStepDto;
import com.irris.yamo.entities.LaundryService;
import com.irris.yamo.entities.ProcessStep;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LaundryServiceMapper {

    private final ProcessStepMapper processStepMapper;

    public LaundryServiceMapper(ProcessStepMapper processStepMapper) {
        this.processStepMapper = processStepMapper;
    }

    public LaundryServiceDto toDto(LaundryService service) {
        if (service == null) {
            return null;
        }

        return LaundryServiceDto.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .pricePerKg(service.getPricePerKg())
                .pricePerItem(service.getPricePerPiece())
                .processSteps(service.getSteps() != null ?
                        service.getSteps().stream()
                                .map(processStepMapper::toDto)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}
