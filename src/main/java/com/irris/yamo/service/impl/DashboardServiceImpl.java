package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.DashboardDto;
import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.enums.OrderStatus;
import com.irris.yamo.entities.enums.TaskStatus;
import com.irris.yamo.entities.enums.TaskType;
import com.irris.yamo.mapper.OrderMapper;
import com.irris.yamo.repositories.*;
import com.irris.yamo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final LogisticTaskRepository logisticTaskRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getOperatorDashboard() {
        DashboardDto dashboard = new DashboardDto();

        // Statistiques des commandes
        dashboard.setPendingReceptionOrders(getPendingReceptionCount());
        dashboard.setInProductionOrders(getInProductionCount());
        dashboard.setReadyForDeliveryOrders(getReadyForDeliveryCount());
        dashboard.setIncompletePaymentOrders(getIncompletePaymentCount());
        
        // Statistiques logistiques
        dashboard.setPendingPickups(logisticTaskRepository
                .findPendingTasksByType(TaskType.PICKUP).size() + 0L);
        dashboard.setPendingDeliveries(logisticTaskRepository
                .findPendingTasksByType(TaskType.DELIVERY).size() + 0L);
        dashboard.setTasksInProgress((long) logisticTaskRepository
                .findByDriverIdAndStatus(null, TaskStatus.IN_PROGRESS).size());

        // Commandes récentes (10 dernières)
        List<Order> recentOrders = orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .limit(10)
                .collect(Collectors.toList());
        dashboard.setRecentOrders(recentOrders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList()));

        return dashboard;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getAdminDashboard() {
        DashboardDto dashboard = getOperatorDashboard();

        // Ajouter les statistiques admin
        dashboard.setTotalOrders(getOrdersCount());
        dashboard.setTotalCustomers(customerRepository.count());
        
        // Revenus
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0);
        
        dashboard.setTotalRevenue(orderRepository.sumAllTotalAmount());
        dashboard.setMonthRevenue(orderRepository.sumTotalAmountBetweenDates(startOfMonth, now));
        
        // Statistiques par statut
        dashboard.setOrdersByStatus(getOrderStatisticsByStatus());
        
        return dashboard;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderStatisticsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        
        for (OrderStatus status : OrderStatus.values()) {
            long count = orderRepository.findByStatus(status).size();
            stats.put(status.name(), count);
        }
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getRevenueByPeriod(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> revenue = new HashMap<>();
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        BigDecimal totalRevenue = orderRepository.sumTotalAmountBetweenDates(start, end);
        revenue.put("total", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        return revenue;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getOrdersByZone() {
        // Grouper les commandes par ville/quartier des clients
        Map<String, Long> zones = new HashMap<>();
        
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            String zone = "Unknown";
            if (order.getCustomer() != null) {
                // Utiliser la ville du client comme zone
                zone = "Zone_" + order.getCustomer().getId(); // À améliorer avec vraie adresse
            }
            zones.put(zone, zones.getOrDefault(zone, 0L) + 1);
        }
        
        return zones;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrdersCount() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPendingReceptionCount() {
        return orderRepository.findByStatus(OrderStatus.CREATED).stream()
                .filter(order -> !order.isWasReceived())
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getInProductionCount() {
        return orderRepository.findByStatus(OrderStatus.IN_PRODUCTION).size() + 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReadyForDeliveryCount() {
        return orderRepository.findOrdersReadyForDelivery().stream()
                .filter(Order::isWasPackaged)
                .filter(Order::isFullyPaid)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getIncompletePaymentCount() {
        return orderRepository.findAll().stream()
                .filter(order -> !order.isFullyPaid())
                .count();
    }
}
