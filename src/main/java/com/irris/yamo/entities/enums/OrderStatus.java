package com.irris.yamo.entities.enums;

/**
 * Statuts d'une commande dans son cycle de vie
 */
public enum OrderStatus {
    // Création et confirmation
    CREATED,                // Commande créée, en cours d'ajout d'articles
    CONFIRMED,              // Commande confirmée, prête pour ramassage
    
    // Ramassage
    PICKUP_SCHEDULED,       // Ramassage planifié
    PICKED_UP,              // Articles ramassés par le livreur
    
    // Réception et production
    RECEIVED,               // Articles réceptionnés au pressing
    IN_PRODUCTION,          // En cours de production
    PRODUCTION_COMPLETED,   // Production terminée
    PRODUCTION_ISSUE,       // Problème de production
    
    // Paiement et livraison
    PENDING_PAYMENT,        // En attente de paiement
    READY,                  // Prêt pour livraison (payé et produit)
    DELIVERY_SCHEDULED,     // Livraison planifiée
    OUT_FOR_DELIVERY,       // En cours de livraison
    
    // Finalisation
    DELIVERED,              // Livré au client
    CANCELLED               // Annulé
}