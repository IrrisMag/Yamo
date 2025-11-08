package com.irris.yamo.repositories;

import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.ArticleCategory;
import com.irris.yamo.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    List<Article> findByOrder(Order order);
    
    List<Article> findByCategory(ArticleCategory category);
    
    @Query("SELECT a FROM Article a WHERE a.order.id = :orderId")
    List<Article> findByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT a FROM Article a JOIN a.instances ai WHERE ai.status = 'PENDING'")
    List<Article> findArticlesWithPendingInstances();
    
    @Query("SELECT a.category, COUNT(a) FROM Article a GROUP BY a.category")
    List<Object[]> countByCategory();
}
