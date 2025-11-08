package com.irris.yamo.service;

import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.Order;

import java.math.BigDecimal;

public interface BillingService {

    void generateInvoice(Long orderId);

    BigDecimal calculateOrderTotalAmount(Order order);

    BigDecimal calculateArticlePrice(Article article);


}
