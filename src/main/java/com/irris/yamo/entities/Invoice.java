package com.irris.yamo.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;
 // DRAFT, ISSUED, PAID, CANCELLED

    private String pdfUrl;         // URL du fichier PDF généré
    private boolean sentToCustomer;
    private LocalDateTime sentDate;
    private String customerEmail;
    private String customerPhone;

    @PrePersist
    protected void onCreate() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
        if (invoiceNumber == null) {
            invoiceNumber = generateInvoiceNumber();
        }
    }

    private String generateInvoiceNumber() {
        return String.format("INV-%tY%tm%td-%d",
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            System.currentTimeMillis() % 10000
        );
    }
}

