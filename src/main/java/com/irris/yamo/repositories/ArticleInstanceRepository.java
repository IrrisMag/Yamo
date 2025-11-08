package com.irris.yamo.repositories;

import com.irris.yamo.entities.ArticleInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleInstanceRepository extends JpaRepository<ArticleInstance, Long> {
    

    @Query("SELECT ai FROM ArticleInstance ai WHERE ai.article.order.id = :orderId")
    List<ArticleInstance> findByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT ai FROM ArticleInstance ai WHERE ai.status = 'IN_PROGRESS' ORDER BY ai.createdDate ASC")
    List<ArticleInstance> findInProgressOrderByCreatedDate();
    
    @Query("SELECT ai FROM ArticleInstance ai WHERE ai.status = 'PENDING' ORDER BY ai.createdDate ASC")
    List<ArticleInstance> findPendingOrderByCreatedDate();
    
    @Query("SELECT ai FROM ArticleInstance ai WHERE ai.completedDate IS NULL AND ai.createdDate < CURRENT_TIMESTAMP")
    List<ArticleInstance> findIncompleteInstances();
    
    // Méthodes pour QualityControlService
    List<ArticleInstance> findByRequiresReworkTrue();

}
