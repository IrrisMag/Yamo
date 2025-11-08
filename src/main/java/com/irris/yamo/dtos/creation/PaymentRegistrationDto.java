package com.irris.yamo.dtos.creation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRegistrationDto {
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
}
