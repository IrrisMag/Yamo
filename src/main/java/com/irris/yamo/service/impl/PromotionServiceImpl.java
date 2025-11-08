package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.PromotionDto;
import com.irris.yamo.dtos.creation.PromotionCreationDto;
import com.irris.yamo.entities.*;
import com.irris.yamo.entities.enums.PromotionTarget;
import com.irris.yamo.entities.enums.PromotionType;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.*;
import com.irris.yamo.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final LaundryServiceRepository laundryServiceRepository;
    private final ArticleCategoryRepository articleCategoryRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PromotionDto createPromotion(PromotionCreationDto dto) {
        Promotion promotion = Promotion.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .type(PromotionType.valueOf(dto.getType()))
                .discountValue(dto.getDiscountValue())
                .minimumOrderAmount(dto.getMinimumOrderAmount())
                .minimumItems(dto.getMinimumItems())
                .target(dto.getTarget() != null ? PromotionTarget.valueOf(dto.getTarget()) : PromotionTarget.ALL_ORDERS)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(false)
                .isVisibleInCarousel(dto.getIsVisibleInCarousel() != null ? dto.getIsVisibleInCarousel() : true)
                .autoApply(dto.getAutoApply() != null ? dto.getAutoApply() : false)
                .promoCode(dto.getPromoCode())
                .maxUsagePerCustomer(dto.getMaxUsagePerCustomer())
                .maxTotalUsage(dto.getMaxTotalUsage())
                .priority(dto.getPriority() != null ? dto.getPriority() : 0)
                .build();

        // Associer services
        if (dto.getApplicableServiceIds() != null && !dto.getApplicableServiceIds().isEmpty()) {
            Set<LaundryService> services = new HashSet<>();
            for (Long serviceId : dto.getApplicableServiceIds()) {
                laundryServiceRepository.findById(serviceId).ifPresent(services::add);
            }
            promotion.setApplicableServices(services);
        }

        // Associer catégories
        if (dto.getApplicableCategoryIds() != null && !dto.getApplicableCategoryIds().isEmpty()) {
            Set<ArticleCategory> categories = new HashSet<>();
            for (Long categoryId : dto.getApplicableCategoryIds()) {
                articleCategoryRepository.findById(categoryId).ifPresent(categories::add);
            }
            promotion.setApplicableCategories(categories);
        }

        promotion = promotionRepository.save(promotion);
        return toDto(promotion);
    }

    @Override
    @Transactional
    public PromotionDto updatePromotion(Long id, PromotionDto dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'ID: " + id));

        if (dto.getTitle() != null) promotion.setTitle(dto.getTitle());
        if (dto.getDescription() != null) promotion.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) promotion.setImageUrl(dto.getImageUrl());
        if (dto.getDiscountValue() != null) promotion.setDiscountValue(dto.getDiscountValue());
        if (dto.getMinimumOrderAmount() != null) promotion.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        if (dto.getStartDate() != null) promotion.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) promotion.setEndDate(dto.getEndDate());

        promotion = promotionRepository.save(promotion);
        return toDto(promotion);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion non trouvée avec l'ID: " + id);
        }
        promotionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionDto getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'ID: " + id));
        return toDto(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> getCarouselPromotions() {
        return promotionRepository.findCarouselPromotions(LocalDateTime.now()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void activatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'ID: " + id));
        promotion.setIsActive(true);
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void deactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'ID: " + id));
        promotion.setIsActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> getApplicablePromotions(Order order) {
        List<Promotion> autoApplyPromotions = promotionRepository.findAutoApplyPromotions(LocalDateTime.now());
        
        return autoApplyPromotions.stream()
                .filter(promo -> isPromotionApplicable(promo, order))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateBestDiscount(Order order, List<PromotionDto> promotions) {
        BigDecimal maxDiscount = BigDecimal.ZERO;
        
        for (PromotionDto promoDto : promotions) {
            BigDecimal discount = calculateDiscountForPromotion(order, promoDto);
            if (discount.compareTo(maxDiscount) > 0) {
                maxDiscount = discount;
            }
        }
        
        return maxDiscount;
    }

    private boolean isPromotionApplicable(Promotion promo, Order order) {
        // Vérifier validité
        if (!promo.isValid()) return false;
        
        // Vérifier usage maximum
        if (promo.hasReachedMaxUsage()) return false;
        
        // Vérifier montant minimum
        if (promo.getMinimumOrderAmount() != null && 
            order.getTotalAmount().compareTo(promo.getMinimumOrderAmount()) < 0) {
            return false;
        }
        
        // Vérifier nombre d'articles minimum
        if (promo.getMinimumItems() != null && 
            order.getArticles().size() < promo.getMinimumItems()) {
            return false;
        }
        
        // Vérifier cible
        if (promo.getTarget() == PromotionTarget.FIRST_ORDER) {
            // Vérifier si c'est la première commande du client
            // TODO: Implémenter la logique
        }
        
        return true;
    }

    private BigDecimal calculateDiscountForPromotion(Order order, PromotionDto promo) {
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal orderTotal = order.getTotalAmount();
        
        if ("PERCENTAGE".equals(promo.getType())) {
            discount = orderTotal.multiply(promo.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        } else if ("FIXED_AMOUNT".equals(promo.getType())) {
            discount = promo.getDiscountValue();
        } else if ("FREE_DELIVERY".equals(promo.getType())) {
            discount = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        }
        
        return discount;
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionDto validatePromoCode(String code, Long customerId) {
        Promotion promotion = promotionRepository.findByPromoCode(code)
                .orElseThrow(() -> new InvalidOperationException("Code promo invalide"));

        UserYamo customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        // Vérifier validité
        if (!promotion.isValid()) {
            throw new InvalidOperationException("Ce code promo n'est plus valide");
        }

        // Vérifier si le code est requis
        if (!promotion.isCodeRequired()) {
            throw new InvalidOperationException("Ce code n'est pas un code promo valide");
        }

        // Vérifier usage maximum global
        if (promotion.hasReachedMaxUsage()) {
            throw new InvalidOperationException("Ce code promo a atteint sa limite d'utilisation");
        }

        // Vérifier usage par client
        if (promotion.getMaxUsagePerCustomer() != null) {
            long customerUsage = promotionUsageRepository.countByPromotionAndCustomer(promotion, customer);
            if (customerUsage >= promotion.getMaxUsagePerCustomer()) {
                throw new InvalidOperationException("Vous avez déjà utilisé ce code le nombre maximum de fois");
            }
        }

        // Vérifier si nouveau client uniquement
        if (promotion.getIsForNewCustomersOnly()) {
            long customerOrderCount = orderRepository.countByCustomer((Customer) customer);
            if (customerOrderCount > 0) {
                throw new InvalidOperationException("Ce code est réservé aux nouveaux clients");
            }
        }

        // Vérifier si VIP uniquement
        if (promotion.getIsForVipCustomersOnly()) {
            if (customer instanceof Customer) {
                Customer cust = (Customer) customer;
                if (!com.irris.yamo.entities.enums.CustomerSegment.VIP.equals(cust.getCustomerSegment())) {
                    throw new InvalidOperationException("Ce code est réservé aux clients VIP");
                }
            }
        }

        // Vérifier éligibilité client
        if (!promotion.isEligible(customer)) {
            throw new InvalidOperationException("Vous n'êtes pas éligible pour ce code promo");
        }

        return toDto(promotion);
    }

    @Override
    @Transactional
    public BigDecimal applyPromoCodeToOrder(Long orderId, String code) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        Promotion promotion = promotionRepository.findByPromoCode(code)
                .orElseThrow(() -> new InvalidOperationException("Code promo invalide"));

        // Valider le code pour ce client
        validatePromoCode(code, order.getCustomer().getId());

        // Vérifier si applicable à cette commande
        if (!isPromotionApplicable(promotion, order)) {
            throw new InvalidOperationException("Cette promotion n'est pas applicable à cette commande");
        }

        // Calculer la réduction
        BigDecimal discount = calculateDiscountForPromotion(order, toDto(promotion));

        // Appliquer la promotion à la commande
        order.setAppliedPromotion(promotion);
        order.setDiscountAmount(discount);
        orderRepository.save(order);

        // Enregistrer l'utilisation
        PromotionUsage usage = PromotionUsage.builder()
                .promotion(promotion)
                .customer(order.getCustomer())
                .order(order)
                .discountAmount(discount)
                .build();
        promotionUsageRepository.save(usage);

        // Incrémenter le compteur d'utilisation
        promotion.incrementUsage();
        promotionRepository.save(promotion);

        return discount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDto> getCustomerPromotions(Long customerId) {
        UserYamo customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream()
                .filter(promo -> promo.getPromoCode() != null)
                .filter(promo -> promo.isEligible(customer))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PromotionDto toDto(Promotion promotion) {
        return PromotionDto.builder()
                .id(promotion.getId())
                .title(promotion.getTitle())
                .description(promotion.getDescription())
                .imageUrl(promotion.getImageUrl())
                .type(promotion.getType() != null ? promotion.getType().name() : null)
                .discountValue(promotion.getDiscountValue())
                .minimumOrderAmount(promotion.getMinimumOrderAmount())
                .minimumItems(promotion.getMinimumItems())
                .target(promotion.getTarget() != null ? promotion.getTarget().name() : null)
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.getIsActive())
                .isVisibleInCarousel(promotion.getIsVisibleInCarousel())
                .autoApply(promotion.getAutoApply())
                .promoCode(promotion.getPromoCode())
                .requiresCode(promotion.getRequiresCode())
                .isForNewCustomersOnly(promotion.getIsForNewCustomersOnly())
                .isForVipCustomersOnly(promotion.getIsForVipCustomersOnly())
                .maxUsagePerCustomer(promotion.getMaxUsagePerCustomer())
                .maxTotalUsage(promotion.getMaxTotalUsage())
                .currentUsageCount(promotion.getCurrentUsageCount())
                .priority(promotion.getPriority())
                .build();
    }
}
