package com.irris.yamo.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    
    @Column(length = 100)
    private String category; // Catégorie de dépense (SALAIRE, LOYER, ELECTRICITE, EAU, etc.)
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;




    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserYamo createdBy;

    private String invoiceNumber;
    private String invoiceUrl;  // URL du document scanné

    private LocalDateTime expenseDate;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;

    private boolean isPaid;
    private String paymentMethod;  // CASH, CARD, ORANGE_MONEY, MTN_MOMO
    private String paymentReference;

    private boolean isRecurring;
    private String recurringPeriod;  // MONTHLY, QUARTERLY, YEARLY

    @PrePersist
    protected void onCreate() {
        if (expenseDate == null) {
            expenseDate = LocalDateTime.now();
        }
    }
}
