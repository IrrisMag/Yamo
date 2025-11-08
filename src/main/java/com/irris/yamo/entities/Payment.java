package com.irris.yamo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.irris.yamo.entities.enums.PaymentMethod;
import com.irris.yamo.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;  // CASH, CARD, ORANGE_MONEY, MTN_MOMO

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status = PaymentStatus.PENDING;

    private String transactionId;  // Référence de la transaction mobile money/banque
    private String paymentReference;  // Numéro unique généré pour ce paiement
    
    @Column(name = "transaction_code")
    private String transactionCode;  // Code de transaction unique

    private String receivedBy;     // Nom de l'opérateur qui a reçu le paiement
    private String notes;

    private boolean isPartial;     // Si c'est un acompte

    @OneToOne
    private Receipt receipt;       // Reçu généré pour ce paiement

    /**
     * Getter pour order - override nécessaire car @JsonBackReference empêche la génération Lombok
     */
    public Order getOrder() {
        return order;
    }
    
    /**
     * Setter pour order - override nécessaire car @JsonBackReference empêche la génération Lombok
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (paymentReference == null) {
            paymentReference = generateReference();
        }
    }

    private String generateReference() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "PAY-" + timestamp.substring(timestamp.length() - 8);
    }
}
