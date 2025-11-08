package com.irris.yamo.service;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Order;

import java.util.List;

public interface OrderDeliveryService {

    List<OrderDto> getReadyToDeliverOrders();
}
