package com.irris.yamo.entities.enums;

/**
 * Types de réclamations
 */
public enum ComplaintType {
    QUALITY("Problème de qualité"),
    DELAY("Retard de livraison"),
    DAMAGE("Article endommagé"),
    MISSING_ITEM("Article manquant"),
    WRONG_SERVICE("Service incorrect"),
    PRICING("Problème de facturation"),
    CUSTOMER_SERVICE("Service client"),
    OTHER("Autre");

    private final String displayName;

    ComplaintType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
