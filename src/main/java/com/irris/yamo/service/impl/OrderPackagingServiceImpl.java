package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.enums.OrderStatus;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.OrderMapper;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.service.OrderPackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderPackagingServiceImpl implements OrderPackagingService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getReadyToPackageOrders() {
        // Récupérer les commandes traitées mais pas encore emballées
        List<Order> orders = orderRepository.findAll();
        
        return orders.stream()
                .filter(Order::isWasProcessed)
                .filter(order -> !order.isWasPackaged())
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void packageOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID: " + orderId));

        // Vérifier que la commande a été traitée
        if (!order.isWasProcessed()) {
            throw new InvalidOperationException(
                    "La commande doit être traitée avant d'être emballée");
        }

        // Vérifier qu'elle n'est pas déjà emballée
        if (order.isWasPackaged()) {
            throw new InvalidOperationException(
                    "Cette commande a déjà été emballée");
        }

        // Marquer la commande comme emballée
        order.setWasPackaged(true);
        
        // Mettre à jour le statut
        if (order.getStatus() == OrderStatus.IN_PRODUCTION || 
            order.getStatus() == OrderStatus.PRODUCTION_COMPLETED) {
            order.setStatus(OrderStatus.READY);
        }

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void packageOrders(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            try {
                packageOrder(orderId);
            } catch (ResourceNotFoundException | InvalidOperationException e) {
                System.err.println("Erreur lors de l'emballage de la commande " + orderId + ": " + e.getMessage());
            }
        }
    }
}
