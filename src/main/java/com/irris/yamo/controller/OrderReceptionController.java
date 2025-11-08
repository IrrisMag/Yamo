package com.irris.yamo.controller;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;
import com.irris.yamo.service.OrderReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reception")
@RequiredArgsConstructor
public class OrderReceptionController {

    private final OrderReceptionService orderReceptionService;

    /**
     * Réceptionner une commande
     */
    @PostMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> receiveOrder(@PathVariable Long orderId) {
        Order order = orderReceptionService.receiveOrder(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Commande réceptionnée avec succès");
        response.put("orderId", orderId);
        response.put("orderReference", order.getReference());
        response.put("wasReceived", order.isWasReceived());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Réceptionner plusieurs commandes
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> receiveOrders(@RequestBody List<Long> orderIds) {
        List<Order> orders = orderReceptionService.receiveOrders(orderIds);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Commandes réceptionnées");
        response.put("receivedCount", orders.size());
        response.put("requestedCount", orderIds.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les commandes en attente de réception
     */
    @GetMapping("/pending")
    public ResponseEntity<List<OrderDto>> getPendingOrders() {
        List<OrderDto> orders = orderReceptionService.getPendingOrders();
        return ResponseEntity.ok(orders);
    }
}
