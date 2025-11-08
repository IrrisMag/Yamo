package com.irris.yamo.entities;

public class GeoUtils {

    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Calcule la distance entre deux points géographiques (formule de Haversine)
     * @return Distance en kilomètres
     */
    public static double haversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 0.0;
        }

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calcule la distance entre deux adresses
     * @return Distance en kilomètres, ou null si coordonnées invalides
     */
    public static Double calculateDistance(Adresse from, Adresse to) {
        if (from == null || to == null) {
            return null;
        }
        if (from.getLatitude() == null || from.getLongitude() == null ||
            to.getLatitude() == null || to.getLongitude() == null) {
            return null;
        }

        return haversineDistance(
            from.getLatitude(),
            from.getLongitude(),
            to.getLatitude(),
            to.getLongitude()
        );
    }
}
