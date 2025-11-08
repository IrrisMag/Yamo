package com.irris.yamo.mapper;

import com.irris.yamo.dtos.AddressDto;
import com.irris.yamo.dtos.creation.AdressCreationDto;
import com.irris.yamo.entities.Adresse;
import com.irris.yamo.entities.enums.AddressType;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDto toDto(Adresse adresse) {
        if (adresse == null) {
            return null;
        }

        return AddressDto.builder()
                .id(adresse.getId())
                .street(adresse.getStreet())
                .city(adresse.getCity())
                .state(adresse.getQuarter())
                .zipCode(adresse.getZip())
                .country(adresse.getCountry())
                .latitude(adresse.getLatitude())
                .longitude(adresse.getLongitude())
                .type(adresse.getAddressType())
                .description(adresse.getNote())
                .build();
    }

    public Adresse toEntity(AdressCreationDto dto) {
        if (dto == null) {
            return null;
        }

        Adresse adresse = new Adresse();
        updateEntityFromDto(adresse, dto);
        return adresse;
    }

    public void updateEntityFromDto(Adresse adresse, AdressCreationDto dto) {
        if (adresse == null || dto == null) {
            return;
        }

        adresse.setStreet(dto.getStreet());
        adresse.setCity(dto.getCity());
        adresse.setQuarter(dto.getState());
        adresse.setZip(dto.getZipCode());
        adresse.setCountry(dto.getCountry());
        adresse.setLatitude(dto.getLatitude());
        adresse.setLongitude(dto.getLongitude());
        adresse.setNote(dto.getDescription());

        if (dto.getType() != null) {
            try {
                adresse.setAddressType(AddressType.valueOf(dto.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                adresse.setAddressType(AddressType.HOME);
            }
        }
    }
}
