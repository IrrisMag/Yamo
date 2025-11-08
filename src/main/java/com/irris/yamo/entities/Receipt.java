package com.irris.yamo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String receiptNumber;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private LocalDateTime issueDate;
    
    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal amount;
    private String paymentMethod;
    private String transactionReference;

    private String pdfUrl;
    private boolean sentToCustomer;
    private LocalDateTime sentDate;

    @PrePersist
    protected void onCreate() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
        if (receiptNumber == null) {
            receiptNumber = generateReceiptNumber();
        }
    }

    private String generateReceiptNumber() {
        return String.format("REC-%tY%tm%td-%d",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                System.currentTimeMillis() % 10000
        );
    }
}
