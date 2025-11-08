package com.irris.yamo.service.impl;

import com.irris.yamo.entities.*;
import com.irris.yamo.entities.enums.BillingMode;
import com.irris.yamo.entities.enums.PromotionType;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.InvoiceRepository;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.repositories.PromotionRepository;
import com.irris.yamo.repositories.PromotionUsageRepository;
import com.irris.yamo.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;

    @Override
    @Transactional
    public void generateInvoice(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID: " + orderId));

        // Vérifier si une facture existe déjà
        if (order.getInvoice() != null) {
            throw new InvalidOperationException(
                    "Une facture existe déjà pour cette commande");
        }

        // Calculer le total de la commande
        BigDecimal subtotal = calculateOrderTotalAmount(order);
        BigDecimal taxAmount = calculateTax(subtotal);
        BigDecimal discountAmount = calculateDiscount(order, subtotal);
        BigDecimal totalAmount = subtotal.add(taxAmount).subtract(discountAmount);

        // Créer la facture
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // Échéance à 30 jours

        // Récupérer les infos du client
        if (order.getCustomer() != null) {
            invoice.setCustomerEmail(order.getCustomer().getEmail());
            invoice.setCustomerPhone(order.getCustomer().getPhoneNumber());
        }

        invoiceRepository.save(invoice);

        // Mettre à jour le montant total de la commande
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotalAmount(Order order) {
        if (order == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        // Calculer le prix de chaque article
        if (order.getArticles() != null) {
            for (Article article : order.getArticles()) {
                BigDecimal articlePrice = calculateArticlePrice(article);
                total = total.add(articlePrice);
            }
        }

        // Ajouter les frais de livraison si présents
        if (order.getDeliveryPrice() != null) {
            total = total.add(order.getDeliveryPrice());
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateArticlePrice(Article article) {
        if (article == null) {
            return BigDecimal.ZERO;
        }

        // Si le prix est déjà défini, le retourner
        if (article.getPrice() != null && article.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            return article.getPrice();
        }

        // Sinon, calculer selon le mode de facturation
        BillingMode billingMode = article.getEffectiveBillingMode();
        Set<LaundryService> services = article.getLaundryServices();

        if (services == null || services.isEmpty()) {
            // Pas de service associé, retourner zéro
            return BigDecimal.ZERO;
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (LaundryService service : services) {
            BigDecimal servicePrice = calculateServicePrice(service, article, billingMode);
            totalPrice = totalPrice.add(servicePrice);
        }

        // Mettre à jour le prix de l'article
        article.setPrice(totalPrice);

        return totalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule le prix d'un service pour un article donné
     */
    private BigDecimal calculateServicePrice(LaundryService service, Article article, BillingMode billingMode) {
        BigDecimal price = BigDecimal.ZERO;

        switch (billingMode) {
            case PIECE:
            case PAR_PIECE:
                // Facturation à la pièce
                if (service.getPricePerPiece() != null) {
                    int quantity = article.getQuantity() != null ? article.getQuantity() : 1;
                    price = service.getPricePerPiece().multiply(BigDecimal.valueOf(quantity));
                }
                break;

            case KG:
            case PAR_KG:
                // Facturation au poids
                if (service.getPricePerKg() != null) {
                    BigDecimal weight = article.getActualWeight() != null && 
                                      article.getActualWeight().compareTo(BigDecimal.ZERO) > 0
                            ? article.getActualWeight()
                            : article.getEstimatedWeight();

                    if (weight != null && weight.compareTo(BigDecimal.ZERO) > 0) {
                        price = service.getPricePerKg().multiply(weight);
                    }
                }
                break;

            case FLAT_RATE:
                // Tarif forfaitaire - prendre le prix par pièce par défaut
                if (service.getPricePerPiece() != null) {
                    price = service.getPricePerPiece();
                }
                break;
        }

        // Appliquer la remise si elle existe
        if (service.getDiscountPercentage() != null && 
            service.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = price.multiply(service.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            price = price.subtract(discount);
        }

        return price;
    }

    /**
     * Calcule les taxes (TVA par exemple)
     */
    private BigDecimal calculateTax(BigDecimal subtotal) {
        // TVA à 0% pour le moment (à adapter selon votre législation)
        BigDecimal taxRate = BigDecimal.valueOf(0.00); // 0%
        return subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule les remises applicables à la commande
     */
    private BigDecimal calculateDiscount(Order order, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;

        // 1. Remise client personnalisée
        if (order.getCustomer() instanceof Customer) {
            Customer customer = (Customer) order.getCustomer();
            if (customer.getCustomDiscount() != null && customer.getCustomDiscount() > 0) {
                BigDecimal customerDiscount = subtotal.multiply(BigDecimal.valueOf(customer.getCustomDiscount()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discount = discount.add(customerDiscount);
            }
        }

        // 2. Promotions appliquées
        BigDecimal promotionDiscount = calculatePromotionDiscount(order, subtotal);
        discount = discount.add(promotionDiscount);

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule la réduction liée aux promotions
     */
    private BigDecimal calculatePromotionDiscount(Order order, BigDecimal subtotal) {
        BigDecimal discount = BigDecimal.ZERO;

        // Si une promotion est déjà appliquée
        if (order.getAppliedPromotion() != null && order.getDiscountAmount() != null) {
            return order.getDiscountAmount();
        }

        // Sinon, chercher et appliquer automatiquement la meilleure promotion
        List<Promotion> autoApplyPromotions = promotionRepository.findAutoApplyPromotions(LocalDateTime.now());
        
        Promotion bestPromotion = null;
        BigDecimal maxDiscount = BigDecimal.ZERO;

        for (Promotion promo : autoApplyPromotions) {
            if (isPromotionApplicable(promo, order, subtotal)) {
                BigDecimal promoDiscount = calculatePromotionDiscountAmount(promo, order, subtotal);
                if (promoDiscount.compareTo(maxDiscount) > 0) {
                    maxDiscount = promoDiscount;
                    bestPromotion = promo;
                }
            }
        }

        // Appliquer la meilleure promotion trouvée
        if (bestPromotion != null && maxDiscount.compareTo(BigDecimal.ZERO) > 0) {
            order.setAppliedPromotion(bestPromotion);
            order.setDiscountAmount(maxDiscount);
            discount = maxDiscount;

            // Enregistrer l'utilisation
            PromotionUsage usage = PromotionUsage.builder()
                    .promotion(bestPromotion)
                    .customer(order.getCustomer())
                    .order(order)
                    .discountAmount(maxDiscount)
                    .build();
            promotionUsageRepository.save(usage);

            // Incrémenter le compteur
            bestPromotion.incrementUsage();
            promotionRepository.save(bestPromotion);
        }

        return discount;
    }

    /**
     * Vérifie si une promotion est applicable à une commande
     */
    private boolean isPromotionApplicable(Promotion promo, Order order, BigDecimal orderTotal) {
        // Vérifier validité
        if (!promo.isValid()) return false;
        
        // Vérifier usage maximum
        if (promo.hasReachedMaxUsage()) return false;
        
        // Vérifier montant minimum
        if (promo.getMinimumOrderAmount() != null && 
            orderTotal.compareTo(promo.getMinimumOrderAmount()) < 0) {
            return false;
        }
        
        // Vérifier nombre d'articles minimum
        if (promo.getMinimumItems() != null && 
            order.getArticles().size() < promo.getMinimumItems()) {
            return false;
        }

        // Vérifier nouveau client
        if (promo.getIsForNewCustomersOnly()) {
            if (order.getCustomer() instanceof Customer) {
                long orderCount = orderRepository.countByCustomer((Customer) order.getCustomer());
                if (orderCount > 1) return false; // >1 car la commande actuelle compte
            }
        }

        // Vérifier client VIP
        if (promo.getIsForVipCustomersOnly()) {
            if (order.getCustomer() instanceof Customer) {
                Customer customer = (Customer) order.getCustomer();
                if (!com.irris.yamo.entities.enums.CustomerSegment.VIP.equals(customer.getCustomerSegment())) return false;
            }
        }

        // Vérifier éligibilité
        if (!promo.isEligible(order.getCustomer())) return false;
        
        return true;
    }

    /**
     * Calcule le montant de réduction d'une promotion
     */
    private BigDecimal calculatePromotionDiscountAmount(Promotion promo, Order order, BigDecimal orderTotal) {
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (promo.getType()) {
            case PERCENTAGE:
                discount = orderTotal.multiply(promo.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
                
            case FIXED_AMOUNT:
                discount = promo.getDiscountValue();
                // Ne pas dépasser le total
                if (discount.compareTo(orderTotal) > 0) {
                    discount = orderTotal;
                }
                break;
                
            case FREE_DELIVERY:
                discount = order.getDeliveryPrice() != null ? 
                    order.getDeliveryPrice() : BigDecimal.ZERO;
                break;
                
            case BUY_X_GET_Y:
                // TODO: Implémenter la logique BUY_X_GET_Y si nécessaire
                break;
        }
        
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}
