package com.irris.yamo.controller;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.dtos.ArticleInstanceDto;
import com.irris.yamo.dtos.creation.ArticleCreationDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreationDto request) {
        Article article = orderService.createArticle(request);
        return new ResponseEntity<>(article, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllArticles() {
        List<ArticleDto> articles = orderService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{articleId}/instances")
    public ResponseEntity<List<ArticleInstanceDto>> getArticleInstances(@PathVariable Long articleId) {
        List<ArticleInstanceDto> instances = orderService.getArticleInstancesByArticleId(articleId);
        return ResponseEntity.ok(instances);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleDto> updateArticle(
            @PathVariable Long articleId,
            @RequestBody ArticleCreationDto request) {
        ArticleDto article = orderService.updateArticleDetails(articleId, request);
        return ResponseEntity.ok(article);
    }

    @PostMapping("/{articleId}/services")
    public ResponseEntity<ArticleDto> addServices(
            @PathVariable Long articleId,
            @RequestBody List<Long> serviceIds) {
        ArticleDto article = orderService.addLaundryServicesToArticle(articleId, serviceIds);
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/{articleId}/services/{serviceId}")
    public ResponseEntity<ArticleDto> removeService(
            @PathVariable Long articleId,
            @PathVariable Long serviceId) {
        ArticleDto article = orderService.removeLaundryServiceFromArticle(articleId, serviceId);
        return ResponseEntity.ok(article);
    }
}
