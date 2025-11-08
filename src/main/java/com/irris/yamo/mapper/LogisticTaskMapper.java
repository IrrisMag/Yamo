package com.irris.yamo.mapper;

import com.irris.yamo.dtos.AddressDto;
import com.irris.yamo.dtos.LogisticTaskDto;
import com.irris.yamo.entities.LogisticTask;
import org.springframework.stereotype.Component;

@Component
public class LogisticTaskMapper {

    public LogisticTaskDto toDto(LogisticTask task) {
        if (task == null) {
            return null;
        }

        AddressDto addressDto = null;
        if (task.getAddress() != null) {
            addressDto = AddressDto.builder()
                    .id(task.getAddress().getId())
                    .street(task.getAddress().getStreet())
                    .city(task.getAddress().getCity())
                    .latitude(task.getAddress().getLatitude())
                    .longitude(task.getAddress().getLongitude())
                    .build();
        }

        return LogisticTaskDto.builder()
                .id(task.getId())
                .driverId(task.getDriver() != null ? task.getDriver().getId() : null)
                .relatedOrderId(task.getOrder() != null ? task.getOrder().getId() : null)
                .taskType(task.getType() != null ? task.getType().name() : null)
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .ContactName(task.getContactName())
                .contactPhone(task.getContactPhone())
                .scheduledDate(task.getScheduledDate())
                .timeFrom(task.getAvailableFrom())
                .timeTo(task.getAvailableTo())
                .addressDto(addressDto)
                .hasArticlesToBeWeighed(task.isContainsWeighting())
                .signaturePath(task.getSignaturePath())
                .photoPaths(task.getPhotoPaths())
                .sequenceOrder(task.getSequenceOrder())
                .build();
    }
}
