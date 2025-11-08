package com.irris.yamo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "income")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    private LocalDateTime occurredAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treasury_id")
    private Treasury treasury; // trésorerie créditée

    private boolean confirmed = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    public void applyToTreasury() {
        if (treasury != null && amount != null) {
            treasury.credit(amount);
        }
    }
}

