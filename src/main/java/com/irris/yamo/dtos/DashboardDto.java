package com.irris.yamo.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    
    // Statistiques générales
    private Long totalOrders;
    private Long pendingReceptionOrders;
    private Long inProductionOrders;
    private Long readyForDeliveryOrders;
    private Long incompletePaymentOrders;
    
    // Statistiques financières
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private BigDecimal pendingPayments;
    
    // Statistiques clients
    private Long totalCustomers;
    private Long newCustomersThisMonth;
    private Long vipCustomers;
    
    // Statistiques logistiques
    private Long pendingPickups;
    private Long pendingDeliveries;
    private Long tasksInProgress;
    
    // Commandes récentes
    private List<OrderDto> recentOrders;
    
    // Graphiques
    private Map<String, Long> ordersByStatus;
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, Long> ordersByZone;
}
