package com.irris.yamo.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryDto {
    private Long id;
    private String paymentMethod;
    private String name;
    private BigDecimal balance;
    private BigDecimal pendingPayments;
    private BigDecimal expectedIncome;
    private BigDecimal projectedBalance;
    private LocalDateTime lastUpdated;
    
    // Pour vue d'ensemble
    private BigDecimal totalBalance;
    private BigDecimal totalCustomerCredit;
    private Map<String, BigDecimal> balanceByPaymentMethod;
}
