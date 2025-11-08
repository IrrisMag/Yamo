package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.enums.OrderStatus;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.OrderMapper;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.service.OrderReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderReceptionServiceImpl implements OrderReceptionService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Order receiveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID: " + orderId));

        // Vérifier que la commande n'est pas déjà reçue
        if (order.isWasReceived()) {
            throw new InvalidOperationException(
                    "Cette commande a déjà été réceptionnée");
        }

        // Marquer la commande comme reçue
        order.setWasReceived(true);

        // Marquer tous les articles comme reçus
        if (order.getArticles() != null) {
            for (Article article : order.getArticles()) {
                article.setWasReceived(true);
            }
        }

        // Mettre à jour le statut si nécessaire
        if (order.getStatus() == OrderStatus.CREATED) {
            order.setStatus(OrderStatus.RECEIVED);
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public List<Order> receiveOrders(List<Long> orderIds) {
        List<Order> receivedOrders = new ArrayList<>();

        for (Long orderId : orderIds) {
            try {
                Order order = receiveOrder(orderId);
                receivedOrders.add(order);
            } catch (ResourceNotFoundException | InvalidOperationException e) {
                // Log l'erreur mais continue avec les autres commandes
                System.err.println("Erreur lors de la réception de la commande " + orderId + ": " + e.getMessage());
            }
        }

        return receivedOrders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getPendingOrders() {
        // Récupérer les commandes créées mais pas encore reçues
        List<Order> orders = orderRepository.findByStatus(OrderStatus.CREATED);
        
        // Filtrer celles qui ne sont pas marquées comme reçues
        return orders.stream()
                .filter(order -> !order.isWasReceived())
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}
