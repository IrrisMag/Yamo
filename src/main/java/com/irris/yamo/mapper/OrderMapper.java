package com.irris.yamo.mapper;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final ArticleMapper articleMapper;

    public OrderMapper(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .orderDate(order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate() : LocalDate.now())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .deliveryPrice(order.getDeliveryPrice())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .appliedPromotionId(order.getAppliedPromotion() != null ? order.getAppliedPromotion().getId() : null)
                .appliedPromotionTitle(order.getAppliedPromotion() != null ? order.getAppliedPromotion().getTitle() : null)
                .appliedPromotionCode(order.getAppliedPromotion() != null ? order.getAppliedPromotion().getPromoCode() : null)
                .articles(order.getArticles() != null ? 
                        order.getArticles().stream()
                                .map(articleMapper::toDto)
                                .collect(Collectors.toList()) : null)
                .wasReceived(order.isWasReceived())
                .wasSorted(order.isWasSorted())
                .wasProcessed(order.isWasProcessed())
                .wasPackaged(order.isWasPackaged())
                .build();
    }
}
