package com.irris.yamo.controller;

import com.irris.yamo.dtos.DashboardDto;
import com.irris.yamo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/operator")
    public ResponseEntity<DashboardDto> getOperatorDashboard() {
        DashboardDto dashboard = dashboardService.getOperatorDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/admin")
    public ResponseEntity<DashboardDto> getAdminDashboard() {
        DashboardDto dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/statistics/orders")
    public ResponseEntity<Map<String, Long>> getOrderStatistics() {
        Map<String, Long> stats = dashboardService.getOrderStatisticsByStatus();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/revenue")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> revenue = dashboardService.getRevenueByPeriod(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/statistics/zones")
    public ResponseEntity<Map<String, Long>> getOrdersByZone() {
        Map<String, Long> zones = dashboardService.getOrdersByZone();
        return ResponseEntity.ok(zones);
    }
}
