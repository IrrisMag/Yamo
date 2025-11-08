package com.irris.yamo.entities.enums;

/**
 * Méthodes de paiement supportées
 * Harmonisé pour correspondre aux frontends (Livreur + Opérateur)
 */
public enum PaymentMethod {
    CASH,           // Espèces
    CARD,           // Carte bancaire (renommé de CREDIT_CARD)
    ORANGE_MONEY,   // Orange Money (séparé de MOBILE_MONEY)
    MTN_MOMO        // MTN Mobile Money (séparé de MOBILE_MONEY)
}

