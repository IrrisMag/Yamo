package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.enums.OrderStatus;
import com.irris.yamo.mapper.OrderMapper;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.service.OrderDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDeliveryServiceImpl implements OrderDeliveryService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getReadyToDeliverOrders() {
        // Récupérer les commandes prêtes à livrer
        List<Order> orders = orderRepository.findOrdersReadyForDelivery();
        
        // Filtrer celles qui sont payées et emballées
        return orders.stream()
                .filter(Order::isWasPackaged)
                .filter(Order::isFullyPaid)
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}
