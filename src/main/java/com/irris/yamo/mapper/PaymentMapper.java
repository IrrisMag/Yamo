package com.irris.yamo.mapper;

import com.irris.yamo.dtos.PaymentDto;
import com.irris.yamo.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .paymentMethod(payment.getMethod() != null ? payment.getMethod().name() : null)
                .amount(payment.getAmount() != null ? payment.getAmount().doubleValue() : 0.0)
                .build();
    }
}
