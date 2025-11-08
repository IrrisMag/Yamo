package com.irris.yamo.repositories;


import com.irris.yamo.entities.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Long> {
    
    boolean existsByName(String name);
    
    List<ArticleCategory> findByIsActiveTrue();
}
