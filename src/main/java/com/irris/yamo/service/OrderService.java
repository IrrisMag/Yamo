package com.irris.yamo.service;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.dtos.ArticleInstanceDto;
import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.dtos.creation.ArticleCreationDto;
import com.irris.yamo.dtos.creation.OrderCreationRequest;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.ProcessStep;

import java.util.List;

public interface OrderService {

    Order createOrder(OrderCreationRequest orderRequest);

    Article createArticle(ArticleCreationDto articleCreationDto);

    List<OrderDto> getAllOrders();

    ArticleDto addLaundryServicesToArticle(Long articleId, List<Long> serviceIds);

    ArticleDto updateArticleDetails(Long articleId, ArticleCreationDto articleDetails);

    ArticleDto removeLaundryServiceFromArticle(Long articleId, Long serviceId);

    void deleteOrder(Long orderId);

    List<ArticleDto> getAllArticles();

    List<OrderDto> getOrdersByCustomerId(Long customerId);

    List<ArticleDto> getArticlesByOrderId(Long orderId);

    List<ArticleInstanceDto> getArticleInstancesByArticleId(Long articleId);

}
