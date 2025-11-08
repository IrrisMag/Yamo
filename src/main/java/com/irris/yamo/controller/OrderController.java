package com.irris.yamo.controller;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.dtos.ArticleInstanceDto;
import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.dtos.creation.ArticleCreationDto;
import com.irris.yamo.dtos.creation.OrderCreationRequest;
import com.irris.yamo.entities.Order;
import com.irris.yamo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationRequest request) {
        Order order = orderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<OrderDto> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/articles")
    public ResponseEntity<List<ArticleDto>> getArticlesByOrderId(@PathVariable Long orderId) {
        List<ArticleDto> articles = orderService.getArticlesByOrderId(orderId);
        return ResponseEntity.ok(articles);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
