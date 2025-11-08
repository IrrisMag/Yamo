package com.irris.yamo.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private Long customerId;
    private LocalDate orderDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal deliveryPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    private List<ArticleDto> articles;

    // Promotion appliquée
    private Long appliedPromotionId;
    private String appliedPromotionTitle;
    private String appliedPromotionCode;

    private boolean wasReceived;
    private boolean wasSorted;
    private boolean wasProcessed;
    private boolean wasPackaged;

}
