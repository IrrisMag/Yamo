package com.irris.yamo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.irris.yamo.entities.enums.OrderStatus;
import com.irris.yamo.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private UserYamo customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Article> articles = new HashSet<>();



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private Invoice invoice;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @Column(name = "is_express")
    private Boolean isExpress = false;

    @Column(name = "promo_code")
    private List<String> promoCodes;

    // Promotion appliquée
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_promotion_id")
    private Promotion appliedPromotion;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LogisticTask> logisticTasks=new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice = BigDecimal.ZERO;


    private LocalDateTime requiredCompletionDate;

    private boolean wasReceived = false;
    private boolean wasSorted = false;
    private boolean wasProcessed = false;
    private boolean wasPackaged = false;
    


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (reference == null) {
            reference = generateReference();
        }
        status = OrderStatus.CREATED;
    }

    public void addArticle(Article article) {
        articles.add(article);
        article.setOrder(this);

    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.setOrder(null);
    }


    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setOrder(this);
    }

    public boolean isFullyPaid() {
        BigDecimal totalPaid = payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalPaid.compareTo(totalAmount) >= 0;
    }


    public boolean isFullyProcessed() {
        return articles.stream()
            .flatMap(article -> article.getInstances().stream())
            .allMatch(ArticleInstance::isCompleted);
    }

    public double getOverallProgress() {
        if (articles.isEmpty()) return 0.0;

        return articles.stream()
            .flatMap(article -> article.getInstances().stream())
            .mapToDouble(ArticleInstance::getProgress)
            .average()
            .orElse(0.0);
    }

    private String generateReference() {
        return String.format("CMD%06d", id);
    }

    public boolean canBeDelivered() {
        return isFullyProcessed() && isFullyPaid() && status == OrderStatus.READY;
    }

    public void updateStatus() {
        if (isFullyProcessed()) {
            if (isFullyPaid()) {
                status = OrderStatus.READY;
            } else {
                status = OrderStatus.PENDING_PAYMENT;
            }
        } else if (getOverallProgress() > 0) {
            status = OrderStatus.IN_PRODUCTION;
        }
    }

    public boolean isLate() {
        return LocalDateTime.now().isAfter(requiredCompletionDate);
    }

    public long getRemainingTimeInHours() {
        if (requiredCompletionDate == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), requiredCompletionDate).toHours();
    }

    /**
     * Alias pour compatibilité avec le code existant
     */
    public String getCode() {
        return reference;
    }

    /**
     * Calcule le montant total incluant le prix de livraison
     */
    public BigDecimal getTotalWithDelivery() {
        return totalAmount.add(deliveryPrice);
    }

    /**
     * Calcule le montant final après réduction
     */
    public BigDecimal getFinalAmount() {
        BigDecimal total = getTotalWithDelivery();
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return total.subtract(discountAmount).max(BigDecimal.ZERO);
        }
        return total;
    }



    /**
     * Vérifie si la commande est en production
     */
    public boolean isInProduction() {
        return status == OrderStatus.IN_PRODUCTION || status == OrderStatus.IN_PRODUCTION;
    }

    /**
     * Calcule le montant total payé
     */
    public BigDecimal getPaidAmount() {
        return payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule le montant restant à payer
     */
    public BigDecimal getRemainingAmount() {
        return getTotalWithDelivery().subtract(getPaidAmount());
    }

    
    /**
     * Calcule dynamiquement le nombre d'articles
     */
    public Integer getActualArticleCount() {
        return articles != null ? articles.size() : 0;
    }
    

}
