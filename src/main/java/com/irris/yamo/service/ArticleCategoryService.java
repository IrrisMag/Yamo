package com.irris.yamo.service;

import com.irris.yamo.dtos.ArticleCategoryDto;
import com.irris.yamo.dtos.creation.ArticleCategoryCreationDto;

import java.util.List;

public interface ArticleCategoryService {

    /**
     * Créer une nouvelle catégorie d'article
     */
    ArticleCategoryDto createCategory(ArticleCategoryCreationDto dto);

    /**
     * Mettre à jour une catégorie d'article
     */
    ArticleCategoryDto updateCategory(Long id, ArticleCategoryCreationDto dto);

    /**
     * Supprimer une catégorie d'article
     */
    void deleteCategory(Long id);

    /**
     * Récupérer une catégorie par ID
     */
    ArticleCategoryDto getCategoryById(Long id);

    /**
     * Récupérer toutes les catégories
     */
    List<ArticleCategoryDto> getAllCategories();

    /**
     * Récupérer les catégories actives
     */
    List<ArticleCategoryDto> getActiveCategories();

    /**
     * Activer/Désactiver une catégorie
     */
    void toggleCategoryStatus(Long id, boolean isActive);
}
