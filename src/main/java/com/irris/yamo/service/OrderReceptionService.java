package com.irris.yamo.service;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.UserYamo;

import java.util.List;

public interface OrderReceptionService {

    Order receiveOrder(Long orderId);

    List<Order> receiveOrders (List<Long> orderIds);

    List<OrderDto> getPendingOrders();
}
