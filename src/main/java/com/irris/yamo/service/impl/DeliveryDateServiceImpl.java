package com.irris.yamo.service.impl;

import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.Order;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.ArticleRepository;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.service.DeliveryDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryDateServiceImpl implements DeliveryDateService {

    private final OrderRepository orderRepository;
    private final ArticleRepository articleRepository;

    // Constantes de délais (en heures)
    private static final int EXPRESS_PROCESSING_HOURS = 24;
    private static final int STANDARD_PROCESSING_HOURS = 72;
    private static final int ARTICLE_BASE_PROCESSING_HOURS = 2;
    private static final int PICKUP_TO_DELIVERY_HOURS = 2;

    // Heures d'ouverture
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime calculateDeliveryDate(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        return calculateDeliveryDateByServiceType(
                order.getIsExpress(),
                order.getArticles() != null ? order.getArticles().size() : 0
        );
    }

    @Override
    public LocalDateTime calculateDeliveryDateByServiceType(boolean isExpress, int articleCount) {
        LocalDateTime now = LocalDateTime.now();

        // Calculer le temps de traitement total
        int processingHours = isExpress ? EXPRESS_PROCESSING_HOURS : STANDARD_PROCESSING_HOURS;
        processingHours += articleCount * ARTICLE_BASE_PROCESSING_HOURS;

        // Ajouter le délai au moment actuel
        LocalDateTime estimatedDate = now.plusHours(processingHours);

        // Ajuster pour les heures ouvrables
        estimatedDate = adjustToBusinessHours(estimatedDate);

        return estimatedDate;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime calculateProductionCompletionDate(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        // Calculer le temps total de traitement des articles
        int totalProcessingHours = 0;
        if (order.getArticles() != null) {
            for (Article article : order.getArticles()) {
                totalProcessingHours += estimateArticleProcessingTimeInHours(article.getId());
            }
        }

        LocalDateTime productionCompletion = LocalDateTime.now().plusHours(totalProcessingHours);
        return adjustToBusinessHours(productionCompletion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderLate(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (order.getRequiredCompletionDate() == null) {
            return false;
        }

        return LocalDateTime.now().isAfter(order.getRequiredCompletionDate()) && !order.isFullyProcessed();
    }

    @Override
    @Transactional(readOnly = true)
    public long getRemainingTimeInHours(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (order.getRequiredCompletionDate() == null) {
            return 0;
        }

        long hours = java.time.Duration.between(LocalDateTime.now(), order.getRequiredCompletionDate()).toHours();
        return Math.max(0, hours);
    }

    @Override
    @Transactional
    public void setCustomDeliveryDate(Long orderId, LocalDateTime deliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        order.setRequiredCompletionDate(deliveryDate);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public Map<String, Object> recalculateLateOrders() {
        List<Order> lateOrders = orderRepository.findAll().stream()
                .filter(o -> o.getRequiredCompletionDate() != null &&
                        LocalDateTime.now().isAfter(o.getRequiredCompletionDate()) &&
                        !o.isFullyProcessed())
                .collect(Collectors.toList());

        int recalculated = 0;
        for (Order order : lateOrders) {
            LocalDateTime newDate = calculateDeliveryDate(order.getId());
            order.setRequiredCompletionDate(newDate);
            orderRepository.save(order);
            recalculated++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalLateOrders", lateOrders.size());
        result.put("recalculated", recalculated);
        result.put("timestamp", LocalDateTime.now());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public int estimateArticleProcessingTimeInHours(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé"));

        int baseTime = ARTICLE_BASE_PROCESSING_HOURS;

        // Ajuster selon le nombre de services
        if (article.getLaundryServices() != null) {
            baseTime += article.getLaundryServices().size() * 1;
        }

        // Ajuster selon la quantité
        if (article.getQuantity() != null && article.getQuantity() > 1) {
            baseTime += (article.getQuantity() - 1);
        }

        return baseTime;
    }

    @Override
    public LocalDateTime calculateOptimalPickupDate(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        // Le ramassage optimal est ASAP pendant les heures ouvrables
        LocalDateTime now = LocalDateTime.now();

        // Si c'est après les heures d'ouverture, programmer pour le lendemain matin
        if (now.toLocalTime().isAfter(CLOSING_TIME)) {
            return now.plusDays(1).with(OPENING_TIME);
        }

        // Si c'est avant les heures d'ouverture, programmer pour ce matin
        if (now.toLocalTime().isBefore(OPENING_TIME)) {
            return now.with(OPENING_TIME);
        }

        // Sinon, dans 2 heures
        LocalDateTime pickupDate = now.plusHours(PICKUP_TO_DELIVERY_HOURS);
        return adjustToBusinessHours(pickupDate);
    }

    // ========== Méthodes Utilitaires ==========

    /**
     * Ajuste une date/heure aux heures ouvrables
     */
    private LocalDateTime adjustToBusinessHours(LocalDateTime dateTime) {
        LocalDateTime adjusted = dateTime;

        // Ignorer les weekends
        while (adjusted.getDayOfWeek() == DayOfWeek.SATURDAY || adjusted.getDayOfWeek() == DayOfWeek.SUNDAY) {
            adjusted = adjusted.plusDays(1);
        }

        // Ajuster l'heure si en dehors des heures d'ouverture
        if (adjusted.toLocalTime().isBefore(OPENING_TIME)) {
            adjusted = adjusted.with(OPENING_TIME);
        } else if (adjusted.toLocalTime().isAfter(CLOSING_TIME)) {
            adjusted = adjusted.plusDays(1).with(OPENING_TIME);
            // Vérifier à nouveau pour les weekends
            while (adjusted.getDayOfWeek() == DayOfWeek.SATURDAY || adjusted.getDayOfWeek() == DayOfWeek.SUNDAY) {
                adjusted = adjusted.plusDays(1);
            }
        }

        return adjusted;
    }
}
