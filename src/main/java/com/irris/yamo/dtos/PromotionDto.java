package com.irris.yamo.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String type;
    private BigDecimal discountValue;
    private BigDecimal minimumOrderAmount;
    private Integer minimumItems;
    private String target;
    private List<Long> applicableServiceIds;
    private List<Long> applicableCategoryIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Boolean isVisibleInCarousel;
    private Boolean autoApply;
    private String promoCode;
    private Boolean requiresCode;
    private Boolean isForNewCustomersOnly;
    private Boolean isForVipCustomersOnly;
    private Integer maxUsagePerCustomer;
    private Integer maxTotalUsage;
    private Integer currentUsageCount;
    private Integer priority;
}
