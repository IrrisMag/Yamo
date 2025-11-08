package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.ArticleCategoryDto;
import com.irris.yamo.dtos.creation.ArticleCategoryCreationDto;
import com.irris.yamo.entities.ArticleCategory;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.ArticleCategoryMapper;
import com.irris.yamo.repositories.ArticleCategoryRepository;
import com.irris.yamo.service.ArticleCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    private final ArticleCategoryRepository categoryRepository;
    private final ArticleCategoryMapper categoryMapper;

    @Override
    @Transactional
    public ArticleCategoryDto createCategory(ArticleCategoryCreationDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new InvalidOperationException(
                    "Une catégorie avec le nom '" + dto.getName() + "' existe déjà");
        }

        ArticleCategory category = ArticleCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .iconUrl(dto.getIconUrl())
                .isActive(true)
                .build();

        ArticleCategory savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public ArticleCategoryDto updateCategory(Long id, ArticleCategoryCreationDto dto) {
        ArticleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée avec l'ID: " + id));

        if (!category.getName().equals(dto.getName()) && 
            categoryRepository.existsByName(dto.getName())) {
            throw new InvalidOperationException(
                    "Une catégorie avec le nom '" + dto.getName() + "' existe déjà");
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIconUrl(dto.getIconUrl());

        ArticleCategory updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ArticleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée avec l'ID: " + id));
        
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleCategoryDto getCategoryById(Long id) {
        ArticleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée avec l'ID: " + id));
        
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleCategoryDto> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleCategoryStatus(Long id, boolean isActive) {
        ArticleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée avec l'ID: " + id));
        
        category.setActive(isActive);
        categoryRepository.save(category);
    }
}
