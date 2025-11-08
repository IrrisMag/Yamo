package com.irris.yamo.controller;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.service.ArticleSortingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sorting")
@RequiredArgsConstructor
public class ArticleSortingController {

    private final ArticleSortingService articleSortingService;

    /**
     * Trier un article
     */
    @PostMapping("/article/{articleId}")
    public ResponseEntity<Map<String, Object>> sortArticle(@PathVariable Long articleId) {
        Article article = articleSortingService.sortArticle(articleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Article trié avec succès");
        response.put("articleId", articleId);
        response.put("articleName", article.getName());
        response.put("wasSorted", article.isWasSorted());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Trier plusieurs articles
     */
    @PostMapping("/articles")
    public ResponseEntity<Map<String, Object>> sortArticles(@RequestBody List<Long> articleIds) {
        List<Article> articles = articleSortingService.sortArticles(articleIds);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Articles triés");
        response.put("sortedCount", articles.size());
        response.put("requestedCount", articleIds.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Vérifier si un article est trié
     */
    @GetMapping("/article/{articleId}/verify")
    public ResponseEntity<Map<String, Boolean>> verifyArticleSorted(@PathVariable Long articleId) {
        boolean isSorted = articleSortingService.verifyArticlesSorted(articleId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isSorted", isSorted);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les articles réceptionnés (à trier)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ArticleDto>> getReceptionnedArticles() {
        List<ArticleDto> articles = articleSortingService.getReceptionnedArticles();
        return ResponseEntity.ok(articles);
    }

    /**
     * Obtenir les articles réceptionnés d'une commande
     */
    @GetMapping("/pending/order/{orderId}")
    public ResponseEntity<List<ArticleDto>> getReceptionnedArticlesForOrder(@PathVariable Long orderId) {
        List<ArticleDto> articles = articleSortingService.getReceptionnedArticlesForOrder(orderId);
        return ResponseEntity.ok(articles);
    }
}
