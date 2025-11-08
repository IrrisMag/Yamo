package com.irris.yamo.service;

import com.irris.yamo.entities.Receipt;

public interface PaymentService {

    void processPayment(Double amount);

    void registerOrderPayment(Long orderId, Double amount);

    void validateOrderPayment(Long orderId, Long paymentId);

    Receipt generateReceipt(Long orderId, Long paymentId);

    void refundPayment(String transactionId);

}
