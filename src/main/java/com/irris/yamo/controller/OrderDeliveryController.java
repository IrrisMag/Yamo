package com.irris.yamo.controller;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.service.OrderDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class OrderDeliveryController {

    private final OrderDeliveryService orderDeliveryService;

    /**
     * Obtenir les commandes prêtes à livrer
     */
    @GetMapping("/ready")
    public ResponseEntity<List<OrderDto>> getReadyToDeliverOrders() {
        List<OrderDto> orders = orderDeliveryService.getReadyToDeliverOrders();
        return ResponseEntity.ok(orders);
    }
}
