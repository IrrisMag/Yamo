package com.irris.yamo.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface DeliveryDateService {

    /**
     * Calcule la date de livraison estimée pour une commande
     */
    LocalDateTime calculateDeliveryDate(Long orderId);

    /**
     * Calcule la date de livraison en fonction du type de service
     */
    LocalDateTime calculateDeliveryDateByServiceType(boolean isExpress, int articleCount);

    /**
     * Calcule la date d'achèvement de production
     */
    LocalDateTime calculateProductionCompletionDate(Long orderId);

    /**
     * Vérifie si une commande est en retard
     */
    boolean isOrderLate(Long orderId);

    /**
     * Calcule le délai restant avant livraison (en heures)
     */
    long getRemainingTimeInHours(Long orderId);

    /**
     * Définit une date de livraison personnalisée
     */
    void setCustomDeliveryDate(Long orderId, LocalDateTime deliveryDate);

    /**
     * Recalcule toutes les dates de livraison en retard
     */
    Map<String, Object> recalculateLateOrders();

    /**
     * Estime le temps de traitement d'un article
     */
    int estimateArticleProcessingTimeInHours(Long articleId);

    /**
     * Calcule la date optimale de ramassage
     */
    LocalDateTime calculateOptimalPickupDate(Long orderId);
}
