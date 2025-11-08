package com.irris.yamo.repositories;

import com.irris.yamo.entities.Promotion;
import com.irris.yamo.entities.enums.PromotionTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    // Trouver par code promo
    Optional<Promotion> findByPromoCode(String promoCode);
    
    // Promotions actives
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true " +
           "AND (p.startDate IS NULL OR p.startDate <= :now) " +
           "AND (p.endDate IS NULL OR p.endDate >= :now)")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);
    
    // Promotions visibles dans le carrousel
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true " +
           "AND p.isVisibleInCarousel = true " +
           "AND (p.startDate IS NULL OR p.startDate <= :now) " +
           "AND (p.endDate IS NULL OR p.endDate >= :now)")
    List<Promotion> findCarouselPromotions(@Param("now") LocalDateTime now);
    
    // Promotions avec application automatique
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true " +
           "AND p.autoApply = true " +
           "AND (p.startDate IS NULL OR p.startDate <= :now) " +
           "AND (p.endDate IS NULL OR p.endDate >= :now) " +
           "ORDER BY p.priority DESC")
    List<Promotion> findAutoApplyPromotions(@Param("now") LocalDateTime now);
    
    // Promotions par cible
    List<Promotion> findByTargetAndIsActiveTrue(PromotionTarget target);
}
