package com.irris.yamo.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private Double amount;

}
