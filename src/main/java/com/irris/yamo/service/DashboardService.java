package com.irris.yamo.service;

import com.irris.yamo.dtos.DashboardDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface DashboardService {
    
    DashboardDto getOperatorDashboard();
    
    DashboardDto getAdminDashboard();
    
    Map<String, Long> getOrderStatisticsByStatus();
    
    Map<String, BigDecimal> getRevenueByPeriod(LocalDate startDate, LocalDate endDate);
    
    Map<String, Long> getOrdersByZone();
    
    Long getOrdersCount();
    
    Long getPendingReceptionCount();
    
    Long getInProductionCount();
    
    Long getReadyForDeliveryCount();
    
    Long getIncompletePaymentCount();
}
