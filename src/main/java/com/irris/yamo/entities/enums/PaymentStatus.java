package com.irris.yamo.entities.enums;

/**
 * Statut d'un paiement
 */
public enum PaymentStatus {
    /**
     * Paiement en attente de traitement
     */
    PENDING,
    
    /**
     * Paiement en cours de traitement
     */
    PROCESSING,
    
    /**
     * Paiement complété avec succès
     */
    COMPLETED,
    
    /**
     * Paiement échoué
     */
    FAILED,
    
    /**
     * Paiement annulé
     */
    CANCELLED,
    
    /**
     * Paiement remboursé
     */
    REFUNDED,
    
    /**
     * Paiement partiellement remboursé
     */
    PARTIALLY_REFUNDED
}
