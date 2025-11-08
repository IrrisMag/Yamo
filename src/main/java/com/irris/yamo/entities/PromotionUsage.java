package com.irris.yamo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private UserYamo customer;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    private LocalDateTime usedAt;
    
    @PrePersist
    protected void onCreate() {
        usedAt = LocalDateTime.now();
    }
}
