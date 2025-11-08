package com.irris.yamo.entities;

import com.irris.yamo.entities.enums.PromotionTarget;
import com.irris.yamo.entities.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Informations basiques
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String imageUrl;  // Pour le carrousel
    
    // Type de promotion
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType type;  // PERCENTAGE, FIXED_AMOUNT, FREE_DELIVERY, BUY_X_GET_Y
    
    // Valeur de la réduction
    @Column(precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    // Conditions d'application
    @Column(precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;
    
    private Integer minimumItems;
    
    // Applicabilité
    @Enumerated(EnumType.STRING)
    private PromotionTarget target;  // ALL_ORDERS, SPECIFIC_SERVICE, SPECIFIC_CATEGORY, FIRST_ORDER
    
    @ManyToMany
    @JoinTable(
        name = "promotion_services",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<LaundryService> applicableServices;
    
    @ManyToMany
    @JoinTable(
        name = "promotion_categories",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<ArticleCategory> applicableCategories;
    
    // Période de validité
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Statut
    @Column(nullable = false)
    private Boolean isActive = false;
    
    @Column(nullable = false)
    private Boolean isVisibleInCarousel = true;
    
    // Application automatique
    @Column(nullable = false)
    private Boolean autoApply = false;
    
    // Code promo optionnel
    @Column(unique = true)
    private String promoCode;
    
    // Nécessite la saisie du code ?
    @Column(nullable = false)
    private Boolean requiresCode = false;
    
    // Limitations
    private Integer maxUsagePerCustomer;
    private Integer maxTotalUsage;
    
    @Column(nullable = false)
    private Integer currentUsageCount = 0;
    
    // Ciblage clients
    @Column(nullable = false)
    private Boolean isForNewCustomersOnly = false;
    
    @Column(nullable = false)
    private Boolean isForVipCustomersOnly = false;
    
    @ManyToMany
    @JoinTable(
        name = "promotion_eligible_customers",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Set<UserYamo> eligibleCustomers = new HashSet<>();
    
    // Priorité
    private Integer priority = 0;
    
    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               (startDate == null || now.isAfter(startDate)) && 
               (endDate == null || now.isBefore(endDate));
    }
    
    public boolean hasReachedMaxUsage() {
        return maxTotalUsage != null && currentUsageCount >= maxTotalUsage;
    }
    
    public void incrementUsage() {
        this.currentUsageCount++;
    }
    
    public boolean isEligible(UserYamo customer) {
        if (eligibleCustomers != null && !eligibleCustomers.isEmpty()) {
            return eligibleCustomers.contains(customer);
        }
        return true; // Si pas de liste spécifique, tout le monde est éligible
    }
    
    public boolean isCodeRequired() {
        return requiresCode != null && requiresCode && promoCode != null;
    }
}
