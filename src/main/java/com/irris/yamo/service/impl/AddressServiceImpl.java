package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.AddressDto;
import com.irris.yamo.dtos.creation.AdressCreationDto;
import com.irris.yamo.entities.Adresse;
import com.irris.yamo.entities.GeoUtils;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.AddressMapper;
import com.irris.yamo.repositories.AddressRepository;
import com.irris.yamo.repositories.UserRepository;
import com.irris.yamo.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressDto createAddress(AdressCreationDto dto) {
        Adresse adresse = addressMapper.toEntity(dto);
        
        // Validation basique
        if (adresse.getStreet() == null || adresse.getCity() == null) {
            throw new InvalidOperationException("La rue et la ville sont obligatoires");
        }
        
        adresse = addressRepository.save(adresse);
        return addressMapper.toDto(adresse);
    }

    @Override
    @Transactional
    public AddressDto updateAddress(Long id, AdressCreationDto dto) {
        Adresse adresse = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée avec l'ID: " + id));

        addressMapper.updateEntityFromDto(adresse, dto);
        adresse = addressRepository.save(adresse);
        return addressMapper.toDto(adresse);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Adresse non trouvée avec l'ID: " + id);
        }
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getAddressById(Long id) {
        Adresse adresse = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée avec l'ID: " + id));
        return addressMapper.toDto(adresse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByUserId(Long userId) {
        UserYamo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByCustomerContact(String contact) {
        UserYamo user = userRepository.findByEmail(contact)
                .or(() -> userRepository.findByPhoneNumber(contact))
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec le contact: " + contact));

        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> findNearbyAddresses(Double latitude, Double longitude, Double radiusKm) {
        if (latitude == null || longitude == null || radiusKm == null) {
            throw new InvalidOperationException("Latitude, longitude et rayon sont requis");
        }

        return addressRepository.findAll().stream()
                .filter(adresse -> {
                    if (adresse.getLatitude() == null || adresse.getLongitude() == null) {
                        return false;
                    }
                    Double distance = GeoUtils.haversineDistance(
                            latitude, longitude,
                            adresse.getLatitude(), adresse.getLongitude()
                    );
                    return distance != null && distance <= radiusKm;
                })
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return false;
        }

        // Validation des champs obligatoires
        if (addressDto.getStreet() == null || addressDto.getStreet().trim().isEmpty()) {
            return false;
        }
        if (addressDto.getCity() == null || addressDto.getCity().trim().isEmpty()) {
            return false;
        }

        // Validation géolocalisation (si fournie)
        if (addressDto.getLatitude() != null && addressDto.getLongitude() != null) {
            if (addressDto.getLatitude() < -90 || addressDto.getLatitude() > 90) {
                return false;
            }
            if (addressDto.getLongitude() < -180 || addressDto.getLongitude() > 180) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateDistance(Long addressId1, Long addressId2) {
        Adresse address1 = addressRepository.findById(addressId1)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse 1 non trouvée"));
        Adresse address2 = addressRepository.findById(addressId2)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse 2 non trouvée"));

        return GeoUtils.calculateDistance(address1, address2);
    }
}
