package com.irris.yamo.service;

import com.irris.yamo.dtos.PromotionDto;
import com.irris.yamo.dtos.creation.PromotionCreationDto;
import com.irris.yamo.entities.Order;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    
    PromotionDto createPromotion(PromotionCreationDto dto);
    
    PromotionDto updatePromotion(Long id, PromotionDto dto);
    
    void deletePromotion(Long id);
    
    PromotionDto getPromotionById(Long id);
    
    List<PromotionDto> getAllPromotions();
    
    List<PromotionDto> getActivePromotions();
    
    List<PromotionDto> getCarouselPromotions();
    
    void activatePromotion(Long id);
    
    void deactivatePromotion(Long id);
    
    List<PromotionDto> getApplicablePromotions(Order order);
    
    BigDecimal calculateBestDiscount(Order order, List<PromotionDto> promotions);
    
    // Gestion des codes promo
    PromotionDto validatePromoCode(String code, Long customerId);
    
    BigDecimal applyPromoCodeToOrder(Long orderId, String code);
    
    List<PromotionDto> getCustomerPromotions(Long customerId);
}
