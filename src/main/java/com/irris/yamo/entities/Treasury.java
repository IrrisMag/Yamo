package com.irris.yamo.entities;


import com.irris.yamo.entities.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Treasury {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Type de trésorerie basé sur la méthode de paiement
     * CASH, CARD, ORANGE_MONEY, MTN_MOMO, ou null pour trésorerie générale
     */
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private PaymentMethod paymentMethod;
    
    /**
     * Nom de la trésorerie (ex: "Caisse Espèces", "Orange Money", "MTN MoMo", "Carte Bancaire")
     */
    @Column(nullable = false)
    private String name;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;                 // Solde actuel
    
    @Column(precision = 15, scale = 2)
    private BigDecimal pendingPayments = BigDecimal.ZERO;         // Paiements en attente (dépenses)
    
    @Column(precision = 15, scale = 2)
    private BigDecimal expectedIncome = BigDecimal.ZERO;          // Revenus attendus (commandes)
    
    @Column(precision = 15, scale = 2)
    private BigDecimal stockValue = BigDecimal.ZERO;              // Valeur actuelle du stock

    private LocalDateTime lastUpdated;

    @Version
    private Long version;                   // Pour la gestion des concurrences

    // Seuils d'alerte configurables
    @Column(precision = 15, scale = 2)
    private BigDecimal minimumBalance = new BigDecimal("500000.00");  // 500,000 FCFA par défaut
    
    @Column(precision = 15, scale = 2)
    private BigDecimal criticalBalance = new BigDecimal("200000.00"); // 200,000 FCFA par défaut

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isBalanceLow() {
        return balance.compareTo(minimumBalance) < 0;
    }

    public boolean isBalanceCritical() {
        return balance.compareTo(criticalBalance) < 0;
    }

    public BigDecimal getProjectedBalance() {
        return balance.subtract(pendingPayments).add(expectedIncome);
    }

    public BigDecimal getTotalAssets() {
        return balance.add(stockValue);
    }
    
    public void credit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }
    
    public void debit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.subtract(amount);
        }
    }
}
