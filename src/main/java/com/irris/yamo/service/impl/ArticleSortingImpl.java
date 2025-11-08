package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.enums.OrderStatus;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.ArticleMapper;
import com.irris.yamo.repositories.ArticleRepository;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.service.ArticleSortingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleSortingImpl implements ArticleSortingService {

    private final ArticleRepository articleRepository;
    private final OrderRepository orderRepository;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional
    public Article sortArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));

        // Vérifier que l'article a été reçu
        if (!article.isWasReceived()) {
            throw new InvalidOperationException(
                    "L'article doit être réceptionné avant d'être trié");
        }

        // Vérifier qu'il n'est pas déjà trié
        if (article.isWasSorted()) {
            throw new InvalidOperationException(
                    "Cet article a déjà été trié");
        }

        // Marquer l'article comme trié
        article.setWasSorted(true);
        article = articleRepository.save(article);

        // Mettre à jour le statut de la commande si tous les articles sont triés
        if (article.getOrder() != null) {
            updateOrderSortingStatus(article.getOrder());
        }

        return article;
    }

    @Override
    @Transactional
    public List<Article> sortArticles(List<Long> articleIds) {
        List<Article> sortedArticles = new ArrayList<>();

        for (Long articleId : articleIds) {
            try {
                Article article = sortArticle(articleId);
                sortedArticles.add(article);
            } catch (ResourceNotFoundException | InvalidOperationException e) {
                System.err.println("Erreur lors du tri de l'article " + articleId + ": " + e.getMessage());
            }
        }

        return sortedArticles;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyArticlesSorted(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));

        return article.isWasSorted();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getReceptionnedArticles() {
        List<Article> articles = articleRepository.findAll();
        
        return articles.stream()
                .filter(Article::isWasReceived)
                .filter(article -> !article.isWasSorted())
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getReceptionnedArticlesForOrder(Long orderId) {
        List<Article> articles = articleRepository.findByOrderId(orderId);
        
        return articles.stream()
                .filter(Article::isWasReceived)
                .filter(article -> !article.isWasSorted())
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getReceptionnedArticlesForOrders(List<Long> orderIds) {
        List<ArticleDto> allArticles = new ArrayList<>();

        for (Long orderId : orderIds) {
            List<ArticleDto> articles = getReceptionnedArticlesForOrder(orderId);
            allArticles.addAll(articles);
        }

        return allArticles;
    }

    /**
     * Met à jour le statut de tri de la commande
     */
    private void updateOrderSortingStatus(Order order) {
        boolean allArticlesSorted = order.getArticles().stream()
                .allMatch(Article::isWasSorted);

        if (allArticlesSorted) {
            order.setWasSorted(true);
            
            // Mettre à jour le statut de la commande
            if (order.getStatus() == OrderStatus.RECEIVED) {
                order.setStatus(OrderStatus.PRODUCTION_COMPLETED);
            }
            
            orderRepository.save(order);
        }
    }
}
