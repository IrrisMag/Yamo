package com.irris.yamo.controller;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.service.OrderPackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/packaging")
@RequiredArgsConstructor
public class OrderPackagingController {

    private final OrderPackagingService orderPackagingService;

    /**
     * Obtenir les commandes prêtes à emballer
     */
    @GetMapping("/ready")
    public ResponseEntity<List<OrderDto>> getReadyToPackageOrders() {
        List<OrderDto> orders = orderPackagingService.getReadyToPackageOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Emballer une commande
     */
    @PostMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> packageOrder(@PathVariable Long orderId) {
        orderPackagingService.packageOrder(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Commande emballée avec succès");
        response.put("orderId", orderId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Emballer plusieurs commandes
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, String>> packageOrders(@RequestBody List<Long> orderIds) {
        orderPackagingService.packageOrders(orderIds);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Commandes emballées");
        response.put("count", String.valueOf(orderIds.size()));
        
        return ResponseEntity.ok(response);
    }
}
