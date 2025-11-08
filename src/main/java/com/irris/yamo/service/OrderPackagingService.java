package com.irris.yamo.service;

import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.Order;

import java.util.List;

public interface OrderPackagingService {

    List<OrderDto> getReadyToPackageOrders();

    void packageOrder(Long orderId);

    void packageOrders (List<Long> orderIds);
}
