package com.irris.yamo.controller;

import com.irris.yamo.service.DeliveryDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery-dates")
@RequiredArgsConstructor
public class DeliveryDateController {

    private final DeliveryDateService deliveryDateService;

    /**
     * Calculer la date de livraison estimée pour une commande
     */
    @GetMapping("/orders/{orderId}/estimate")
    public ResponseEntity<Map<String, Object>> calculateDeliveryDate(@PathVariable Long orderId) {
        LocalDateTime deliveryDate = deliveryDateService.calculateDeliveryDate(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("estimatedDeliveryDate", deliveryDate);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculer la date selon le type de service
     */
    @GetMapping("/estimate")
    public ResponseEntity<Map<String, Object>> estimateByServiceType(
            @RequestParam boolean isExpress,
            @RequestParam int articleCount) {
        LocalDateTime deliveryDate = deliveryDateService.calculateDeliveryDateByServiceType(isExpress, articleCount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isExpress", isExpress);
        response.put("articleCount", articleCount);
        response.put("estimatedDeliveryDate", deliveryDate);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculer la date d'achèvement de production
     */
    @GetMapping("/orders/{orderId}/production-completion")
    public ResponseEntity<Map<String, Object>> calculateProductionCompletion(@PathVariable Long orderId) {
        LocalDateTime completionDate = deliveryDateService.calculateProductionCompletionDate(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("productionCompletionDate", completionDate);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Vérifier si une commande est en retard
     */
    @GetMapping("/orders/{orderId}/is-late")
    public ResponseEntity<Map<String, Object>> checkIfLate(@PathVariable Long orderId) {
        boolean isLate = deliveryDateService.isOrderLate(orderId);
        long remainingHours = deliveryDateService.getRemainingTimeInHours(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("isLate", isLate);
        response.put("remainingTimeInHours", remainingHours);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir le temps restant avant livraison
     */
    @GetMapping("/orders/{orderId}/remaining-time")
    public ResponseEntity<Map<String, Object>> getRemainingTime(@PathVariable Long orderId) {
        long hours = deliveryDateService.getRemainingTimeInHours(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("remainingTimeInHours", hours);
        response.put("remainingTimeInDays", hours / 24.0);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Définir une date de livraison personnalisée
     */
    @PutMapping("/orders/{orderId}/custom-date")
    public ResponseEntity<Map<String, String>> setCustomDeliveryDate(
            @PathVariable Long orderId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deliveryDate) {
        deliveryDateService.setCustomDeliveryDate(orderId, deliveryDate);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Date de livraison personnalisée définie");
        response.put("deliveryDate", deliveryDate.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Recalculer toutes les commandes en retard
     */
    @PostMapping("/recalculate-late-orders")
    public ResponseEntity<Map<String, Object>> recalculateLateOrders() {
        Map<String, Object> result = deliveryDateService.recalculateLateOrders();
        return ResponseEntity.ok(result);
    }

    /**
     * Estimer le temps de traitement d'un article
     */
    @GetMapping("/articles/{articleId}/processing-time")
    public ResponseEntity<Map<String, Object>> estimateArticleProcessingTime(@PathVariable Long articleId) {
        int hours = deliveryDateService.estimateArticleProcessingTimeInHours(articleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("articleId", articleId);
        response.put("processingTimeInHours", hours);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculer la date optimale de ramassage
     */
    @GetMapping("/orders/{orderId}/optimal-pickup")
    public ResponseEntity<Map<String, Object>> calculateOptimalPickup(@PathVariable Long orderId) {
        LocalDateTime pickupDate = deliveryDateService.calculateOptimalPickupDate(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("optimalPickupDate", pickupDate);
        
        return ResponseEntity.ok(response);
    }
}
