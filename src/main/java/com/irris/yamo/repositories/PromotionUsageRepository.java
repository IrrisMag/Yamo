package com.irris.yamo.repositories;

import com.irris.yamo.entities.Promotion;
import com.irris.yamo.entities.PromotionUsage;
import com.irris.yamo.entities.UserYamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {
    
    List<PromotionUsage> findByPromotion(Promotion promotion);
    
    List<PromotionUsage> findByCustomer(UserYamo customer);
    
    long countByPromotionAndCustomer(Promotion promotion, UserYamo customer);
    
    long countByPromotion(Promotion promotion);
}
