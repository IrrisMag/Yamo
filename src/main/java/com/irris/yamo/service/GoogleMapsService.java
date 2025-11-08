package com.irris.yamo.service;

import java.util.Map;

public interface GoogleMapsService {

    /**
     * Géocode une adresse (obtenir lat/long)
     */
    Map<String, Double> geocodeAddress(String address);

    /**
     * Reverse geocode (obtenir adresse depuis coordonnées)
     */
    String reverseGeocode(Double latitude, Double longitude);

    /**
     * Calculer la distance entre deux points
     */
    Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2);

    /**
     * Calculer la distance et durée de trajet
     */
    Map<String, Object> getDirections(Double originLat, Double originLon, Double destLat, Double destLon);

    /**
     * Obtenir les adresses à proximité
     */
    Map<String, Object> getNearbyPlaces(Double latitude, Double longitude, int radius, String type);

    /**
     * Valider une adresse
     */
    boolean validateAddress(String address);
}
