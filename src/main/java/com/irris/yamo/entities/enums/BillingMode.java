package com.irris.yamo.entities.enums;

public enum BillingMode {
    PIECE("À la pièce"),
    KG("Au kilogramme"),
    FLAT_RATE("Tarif forfaitaire"),

    // Alias pour compatibilité
    PAR_PIECE("À la pièce"),
    PAR_KG("Au kilogramme");

    private final String displayName;

    BillingMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
