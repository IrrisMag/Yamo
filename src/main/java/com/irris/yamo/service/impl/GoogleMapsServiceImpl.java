package com.irris.yamo.service.impl;

import com.irris.yamo.service.GoogleMapsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GoogleMapsServiceImpl implements GoogleMapsService {

    @Value("${google.maps.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GEOCODING_API = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String PLACES_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    @Override
    public Map<String, Double> geocodeAddress(String address) {
        try {
            String url = String.format("%s?address=%s&key=%s",
                    GEOCODING_API,
                    address.replace(" ", "+"),
                    apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                Map<String, Object> results = (Map<String, Object>) ((java.util.List<?>) response.get("results")).get(0);
                Map<String, Object> geometry = (Map<String, Object>) results.get("geometry");
                Map<String, Double> location = (Map<String, Double>) geometry.get("location");

                Map<String, Double> coords = new HashMap<>();
                coords.put("latitude", location.get("lat"));
                coords.put("longitude", location.get("lng"));

                log.info("Géocodage réussi pour: {} → {}", address, coords);
                return coords;
            }

            log.warn("Échec géocodage pour: {}", address);
            return null;

        } catch (Exception e) {
            log.error("Erreur géocodage: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String reverseGeocode(Double latitude, Double longitude) {
        try {
            String url = String.format("%s?latlng=%f,%f&key=%s",
                    GEOCODING_API,
                    latitude,
                    longitude,
                    apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                Map<String, Object> results = (Map<String, Object>) ((java.util.List<?>) response.get("results")).get(0);
                String formattedAddress = (String) results.get("formatted_address");

                log.info("Reverse géocodage réussi: {},{} → {}", latitude, longitude, formattedAddress);
                return formattedAddress;
            }

            log.warn("Échec reverse géocodage pour: {},{}", latitude, longitude);
            return null;

        } catch (Exception e) {
            log.error("Erreur reverse géocodage: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // Utilise la formule de Haversine (distance à vol d'oiseau)
        final int EARTH_RADIUS = 6371; // Rayon de la Terre en km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    @Override
    public Map<String, Object> getDirections(Double originLat, Double originLon, Double destLat, Double destLon) {
        try {
            String url = String.format("%s?origin=%f,%f&destination=%f,%f&key=%s",
                    DIRECTIONS_API,
                    originLat, originLon,
                    destLat, destLon,
                    apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                Map<String, Object> route = (Map<String, Object>) ((java.util.List<?>) response.get("routes")).get(0);
                Map<String, Object> leg = (Map<String, Object>) ((java.util.List<?>) route.get("legs")).get(0);

                Map<String, Object> distance = (Map<String, Object>) leg.get("distance");
                Map<String, Object> duration = (Map<String, Object>) leg.get("duration");

                Map<String, Object> result = new HashMap<>();
                result.put("distanceKm", ((Number) distance.get("value")).doubleValue() / 1000.0);
                result.put("distanceText", distance.get("text"));
                result.put("durationMinutes", ((Number) duration.get("value")).intValue() / 60);
                result.put("durationText", duration.get("text"));

                log.info("Calcul itinéraire réussi: {} km, {} min", result.get("distanceKm"), result.get("durationMinutes"));
                return result;
            }

            log.warn("Échec calcul itinéraire");
            return null;

        } catch (Exception e) {
            log.error("Erreur calcul itinéraire: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> getNearbyPlaces(Double latitude, Double longitude, int radius, String type) {
        try {
            String url = String.format("%s?location=%f,%f&radius=%d&type=%s&key=%s",
                    PLACES_API,
                    latitude, longitude,
                    radius,
                    type,
                    apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                log.info("Lieux à proximité trouvés");
                return response;
            }

            log.warn("Aucun lieu trouvé à proximité");
            return null;

        } catch (Exception e) {
            log.error("Erreur recherche lieux: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateAddress(String address) {
        Map<String, Double> coords = geocodeAddress(address);
        return coords != null && coords.containsKey("latitude") && coords.containsKey("longitude");
    }
}
