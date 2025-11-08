package com.irris.yamo.controller;

import com.irris.yamo.dtos.PromotionDto;
import com.irris.yamo.dtos.creation.PromotionCreationDto;
import com.irris.yamo.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionDto> createPromotion(@RequestBody PromotionCreationDto dto) {
        PromotionDto promotion = promotionService.createPromotion(dto);
        return new ResponseEntity<>(promotion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionDto> updatePromotion(
            @PathVariable Long id,
            @RequestBody PromotionDto dto) {
        PromotionDto promotion = promotionService.updatePromotion(id, dto);
        return ResponseEntity.ok(promotion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDto> getPromotion(@PathVariable Long id) {
        PromotionDto promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping
    public ResponseEntity<List<PromotionDto>> getAllPromotions() {
        List<PromotionDto> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionDto>> getActivePromotions() {
        List<PromotionDto> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/carousel")
    public ResponseEntity<List<PromotionDto>> getCarouselPromotions() {
        List<PromotionDto> promotions = promotionService.getCarouselPromotions();
        return ResponseEntity.ok(promotions);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activatePromotion(@PathVariable Long id) {
        promotionService.activatePromotion(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Promotion activée");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivatePromotion(@PathVariable Long id) {
        promotionService.deactivatePromotion(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Promotion désactivée");
        
        return ResponseEntity.ok(response);
    }

    // Gestion des codes promo
    
    @PostMapping("/validate-code")
    public ResponseEntity<PromotionDto> validatePromoCode(
            @RequestParam String code,
            @RequestParam Long customerId) {
        PromotionDto promotion = promotionService.validatePromoCode(code, customerId);
        return ResponseEntity.ok(promotion);
    }

    @PostMapping("/orders/{orderId}/apply-code")
    public ResponseEntity<Map<String, Object>> applyPromoCode(
            @PathVariable Long orderId,
            @RequestParam String code) {
        BigDecimal discount = promotionService.applyPromoCodeToOrder(orderId, code);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Code promo appliqué avec succès");
        response.put("discount", discount);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/codes")
    public ResponseEntity<List<PromotionDto>> getCustomerPromotions(@PathVariable Long customerId) {
        List<PromotionDto> promotions = promotionService.getCustomerPromotions(customerId);
        return ResponseEntity.ok(promotions);
    }
}
