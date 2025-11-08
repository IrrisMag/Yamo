package com.irris.yamo.controller;

import com.irris.yamo.dtos.AddressDto;
import com.irris.yamo.dtos.creation.AdressCreationDto;
import com.irris.yamo.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Créer une nouvelle adresse
     */
    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@RequestBody AdressCreationDto dto) {
        AddressDto address = addressService.createAddress(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    /**
     * Mettre à jour une adresse
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @RequestBody AdressCreationDto dto) {
        AddressDto address = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(address);
    }

    /**
     * Supprimer une adresse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer une adresse par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long id) {
        AddressDto address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    /**
     * Récupérer toutes les adresses
     */
    @GetMapping
    public ResponseEntity<List<AddressDto>> getAllAddresses() {
        List<AddressDto> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    /**
     * Récupérer les adresses d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressDto>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDto> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Récupérer les adresses d'un client par contact
     */
    @GetMapping("/customer/{contact}")
    public ResponseEntity<List<AddressDto>> getCustomerAddresses(@PathVariable String contact) {
        List<AddressDto> addresses = addressService.getAddressesByCustomerContact(contact);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Trouver les adresses à proximité
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<AddressDto>> findNearbyAddresses(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        List<AddressDto> addresses = addressService.findNearbyAddresses(latitude, longitude, radiusKm);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Calculer la distance entre deux adresses
     */
    @GetMapping("/distance")
    public ResponseEntity<Double> calculateDistance(
            @RequestParam Long addressId1,
            @RequestParam Long addressId2) {
        Double distance = addressService.calculateDistance(addressId1, addressId2);
        return ResponseEntity.ok(distance);
    }

    /**
     * Valider une adresse
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateAddress(@RequestBody AddressDto dto) {
        boolean isValid = addressService.validateAddress(dto);
        return ResponseEntity.ok(isValid);
    }
}
