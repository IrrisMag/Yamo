package com.irris.yamo.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.irris.yamo.entities.enums.BillingMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"order", "laundryServices", "instances"})
@ToString(exclude = {"order", "laundryServices", "instances"})
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    private Integer quantity;
    private String code;
    
    @Column(precision = 10, scale = 3)
    private BigDecimal estimatedWeight;
    
    @Column(precision = 10, scale = 3)
    private BigDecimal actualWeight;

    private String size;
    private String color;
    private String brand;
    private String material; // Matière (ex: "Coton", "Soie", "Polyester", "Laine")

    private boolean wasReceived = false;
    private boolean wasSorted = false;
    private boolean wasProcessed = false;
    private boolean wasPackaged = false;


    @OneToOne
    private ArticleCategory category;
    
    /**
     * Mode de facturation spécifique à cet article
     */
    @Enumerated(EnumType.STRING)
    private BillingMode billingMode;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ArticleInstance> instances = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "article_service_types",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    @JsonIgnore
    private Set<LaundryService> laundryServices = new HashSet<>();


    public void addLaundryService(LaundryService laundryService) {
        laundryServices.add(laundryService);
    }

    public void removeLaundryService(LaundryService laundryService) {
        laundryServices.remove(laundryService);
    }

    public void createInstances() {
        for (int i = 1; i <= quantity; i++) {
            ArticleInstance instance = new ArticleInstance();
            instance.setArticle(this);
            instance.setSize(this.getSize());
            instance.setBrand(this.getBrand());
            instance.setColor(this.getColor());
            instance.setCode(this.getCode() + "-" + i);
            instance.setMaterial(this.getMaterial());

            instances.add(instance);
        }
    }

    // MEDIUM PRIORITY: Deprecated method removed - use PricingCalculator.calculateArticlePrice() instead

    /**
     * Obtient le mode de facturation effectif de l'article
     * Par défaut: PAR_PIECE si non défini
     */
    public BillingMode getEffectiveBillingMode() {
        return this.billingMode != null ? this.billingMode : BillingMode.PAR_PIECE;
    }

    public boolean isCompleted() {
        return !instances.isEmpty() &&
                instances.stream().allMatch(ArticleInstance::isCompleted);
    }

    public double getProgress() {
        if (instances.isEmpty()) return 0.0;
        return instances.stream()
                .mapToDouble(ArticleInstance::getProgress)
                .average()
                .orElse(0.0);
    }


}
