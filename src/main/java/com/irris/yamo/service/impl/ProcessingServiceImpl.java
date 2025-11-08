package com.irris.yamo.service.impl;

import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.ProcessStepTracking;
import com.irris.yamo.entities.enums.ProcessingStatus;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.ArticleInstanceRepository;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.repositories.ProcessStepTrackingRepository;
import com.irris.yamo.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {

    private final ArticleInstanceRepository articleInstanceRepository;
    private final ProcessStepTrackingRepository processStepTrackingRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ArticleInstance> getArticleInstancesByProcessStep(Long processStepId) {
        // Récupérer toutes les instances qui ont ce processStep
        return processStepTrackingRepository.findAll().stream()
                .filter(tracking -> tracking.getProcessStep() != null && 
                                  tracking.getProcessStep().getId().equals(processStepId))
                .map(ProcessStepTracking::getArticleInstance)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markArticleInstanceAsProcessed(Long articleInstanceId) {
        ArticleInstance instance = articleInstanceRepository.findById(articleInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instance d'article non trouvée avec l'ID: " + articleInstanceId));

        // Vérifier que toutes les étapes sont complétées
        if (instance.getTrackings() == null || instance.getTrackings().isEmpty()) {
            throw new InvalidOperationException(
                    "Aucune étape de traitement définie pour cette instance");
        }

        boolean allCompleted = instance.getTrackings().stream()
                .allMatch(ProcessStepTracking::isCompleted);

        if (!allCompleted) {
            throw new InvalidOperationException(
                    "Toutes les étapes de traitement doivent être complétées");
        }

        // Marquer l'instance et son article comme traités
        if (instance.getArticle() != null) {
            instance.getArticle().setWasProcessed(true);
            
            // Vérifier si toutes les instances de l'article sont complétées
            updateArticleProcessingStatus(instance.getArticle().getId());
        }

        articleInstanceRepository.save(instance);
    }

    @Override
    @Transactional
    public void markArticleInstancesAsProcessed(List<Long> articleInstanceIds) {
        for (Long instanceId : articleInstanceIds) {
            try {
                markArticleInstanceAsProcessed(instanceId);
            } catch (ResourceNotFoundException | InvalidOperationException e) {
                System.err.println("Erreur lors du traitement de l'instance " + instanceId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Met à jour le statut de traitement de l'article et de la commande
     */
    private void updateArticleProcessingStatus(Long articleId) {
        articleInstanceRepository.findById(articleId).ifPresent(instance -> {
            if (instance.getArticle() != null && instance.getArticle().getOrder() != null) {
                Order order = instance.getArticle().getOrder();
                
                // Vérifier si tous les articles de la commande sont traités
                boolean allProcessed = order.getArticles().stream()
                        .allMatch(article -> article.isWasProcessed());
                
                if (allProcessed) {
                    order.setWasProcessed(true);
                    orderRepository.save(order);
                }
            }
        });
    }
}
