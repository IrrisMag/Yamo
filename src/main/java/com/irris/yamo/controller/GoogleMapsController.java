package com.irris.yamo.controller;

import com.irris.yamo.service.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    /**
     * Géocoder une adresse
     */
    @GetMapping("/geocode")
    public ResponseEntity<Map<String, Double>> geocodeAddress(@RequestParam String address) {
        Map<String, Double> coords = googleMapsService.geocodeAddress(address);
        
        if (coords != null) {
            return ResponseEntity.ok(coords);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Reverse géocode (coordonnées → adresse)
     */
    @GetMapping("/reverse-geocode")
    public ResponseEntity<Map<String, String>> reverseGeocode(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        String address = googleMapsService.reverseGeocode(latitude, longitude);
        
        if (address != null) {
            Map<String, String> response = new HashMap<>();
            response.put("address", address);
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Calculer distance entre deux points
     */
    @GetMapping("/distance")
    public ResponseEntity<Map<String, Double>> calculateDistance(
            @RequestParam Double lat1,
            @RequestParam Double lon1,
            @RequestParam Double lat2,
            @RequestParam Double lon2) {
        Double distance = googleMapsService.calculateDistance(lat1, lon1, lat2, lon2);
        
        Map<String, Double> response = new HashMap<>();
        response.put("distanceKm", distance);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir itinéraire avec distance et durée
     */
    @GetMapping("/directions")
    public ResponseEntity<Map<String, Object>> getDirections(
            @RequestParam Double originLat,
            @RequestParam Double originLon,
            @RequestParam Double destLat,
            @RequestParam Double destLon) {
        Map<String, Object> directions = googleMapsService.getDirections(originLat, originLon, destLat, destLon);
        
        if (directions != null) {
            return ResponseEntity.ok(directions);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Trouver lieux à proximité
     */
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1000") int radius,
            @RequestParam(defaultValue = "restaurant") String type) {
        Map<String, Object> places = googleMapsService.getNearbyPlaces(latitude, longitude, radius, type);
        
        if (places != null) {
            return ResponseEntity.ok(places);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Valider une adresse
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateAddress(@RequestBody Map<String, String> request) {
        String address = request.get("address");
        boolean isValid = googleMapsService.validateAddress(address);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);
        
        return ResponseEntity.ok(response);
    }
}
