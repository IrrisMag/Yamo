package com.irris.yamo.service;

import com.irris.yamo.dtos.AddressDto;
import com.irris.yamo.dtos.creation.AdressCreationDto;

import java.util.List;

public interface AddressService {

    /**
     * Crée une nouvelle adresse
     */
    AddressDto createAddress(AdressCreationDto dto);

    /**
     * Met à jour une adresse existante
     */
    AddressDto updateAddress(Long id, AdressCreationDto dto);

    /**
     * Supprime une adresse
     */
    void deleteAddress(Long id);

    /**
     * Récupère une adresse par ID
     */
    AddressDto getAddressById(Long id);

    /**
     * Récupère toutes les adresses
     */
    List<AddressDto> getAllAddresses();

    /**
     * Récupère les adresses d'un utilisateur
     */
    List<AddressDto> getAddressesByUserId(Long userId);

    /**
     * Récupère les adresses d'un client par email/téléphone
     */
    List<AddressDto> getAddressesByCustomerContact(String contact);

    /**
     * Trouve les adresses proches d'une position
     */
    List<AddressDto> findNearbyAddresses(Double latitude, Double longitude, Double radiusKm);

    /**
     * Valide une adresse (géolocalisation)
     */
    boolean validateAddress(AddressDto addressDto);

    /**
     * Calcule la distance entre deux adresses
     */
    Double calculateDistance(Long addressId1, Long addressId2);
}
