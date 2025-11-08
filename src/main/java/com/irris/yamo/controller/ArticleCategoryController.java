package com.irris.yamo.controller;

import com.irris.yamo.dtos.ArticleCategoryDto;
import com.irris.yamo.dtos.creation.ArticleCategoryCreationDto;
import com.irris.yamo.service.ArticleCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/article-categories")
@RequiredArgsConstructor
public class ArticleCategoryController {

    private final ArticleCategoryService categoryService;

    /**
     * Créer une nouvelle catégorie d'article (ADMIN)
     */
    @PostMapping
    public ResponseEntity<ArticleCategoryDto> createCategory(
            @RequestBody ArticleCategoryCreationDto dto) {
        ArticleCategoryDto category = categoryService.createCategory(dto);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    /**
     * Mettre à jour une catégorie (ADMIN)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArticleCategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody ArticleCategoryCreationDto dto) {
        ArticleCategoryDto category = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(category);
    }

    /**
     * Supprimer une catégorie (ADMIN)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Catégorie supprimée avec succès");
        response.put("categoryId", id.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer une catégorie par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleCategoryDto> getCategory(@PathVariable Long id) {
        ArticleCategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Récupérer toutes les catégories (ADMIN)
     */
    @GetMapping
    public ResponseEntity<List<ArticleCategoryDto>> getAllCategories() {
        List<ArticleCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupérer les catégories actives (PUBLIC)
     */
    @GetMapping("/active")
    public ResponseEntity<List<ArticleCategoryDto>> getActiveCategories() {
        List<ArticleCategoryDto> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Activer une catégorie (ADMIN)
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activateCategory(@PathVariable Long id) {
        categoryService.toggleCategoryStatus(id, true);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Catégorie activée");
        response.put("categoryId", id.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Désactiver une catégorie (ADMIN)
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateCategory(@PathVariable Long id) {
        categoryService.toggleCategoryStatus(id, false);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Catégorie désactivée");
        response.put("categoryId", id.toString());
        
        return ResponseEntity.ok(response);
    }
}
