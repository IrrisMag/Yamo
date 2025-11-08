package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.dtos.ArticleInstanceDto;
import com.irris.yamo.dtos.OrderDto;
import com.irris.yamo.dtos.creation.ArticleCreationDto;
import com.irris.yamo.dtos.creation.OrderCreationRequest;
import com.irris.yamo.entities.*;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.ArticleInstanceMapper;
import com.irris.yamo.mapper.ArticleMapper;
import com.irris.yamo.mapper.OrderMapper;
import com.irris.yamo.repositories.*;
import com.irris.yamo.service.BillingService;
import com.irris.yamo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ArticleRepository articleRepository;
    private final ArticleInstanceRepository articleInstanceRepository;
    private final LaundryServiceRepository laundryServiceRepository;
    private final ProcessStepRepository processStepRepository;
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final ArticleCategoryRepository articleCategoryRepository;
    
    private final OrderMapper orderMapper;
    private final ArticleMapper articleMapper;
    private final ArticleInstanceMapper articleInstanceMapper;
    
    private final BillingService billingService;

    @Override
    @Transactional
    public Order createOrder(OrderCreationRequest orderRequest) {
        // Vérifier que le client existe
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'ID: " + orderRequest.getCustomerId()));

        // Créer la commande
        Order order = Order.builder()
                .customer(customer)
                .isExpress(false)
                .build();

        // Sauvegarder la commande d'abord pour obtenir un ID
        order = orderRepository.save(order);

        // Créer les articles si présents
        if (orderRequest.getArticles() != null && !orderRequest.getArticles().isEmpty()) {
            for (ArticleCreationDto articleDto : orderRequest.getArticles()) {
                Article article = createArticleForOrder(articleDto, order);
                order.addArticle(article);
            }
        }

        // Calculer le montant total
        calculateOrderTotal(order);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Article createArticle(ArticleCreationDto articleCreationDto) {
        Article article = Article.builder()
                .name(articleCreationDto.getName())
                .description(articleCreationDto.getDescription())
                .quantity(articleCreationDto.getQuantity())
                .material(articleCreationDto.getMaterial())
                .color(articleCreationDto.getColor())
                .size(articleCreationDto.getSize())
                .billingMode(articleCreationDto.getBillingMode())
                .estimatedWeight(articleCreationDto.getEstimatedWeight() != null ? 
                        BigDecimal.valueOf(articleCreationDto.getEstimatedWeight()) : null)
                .build();

        // Associer la catégorie si présente
        if (articleCreationDto.getCategoryId() != null) {
            ArticleCategory category = articleCategoryRepository.findById(articleCreationDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Catégorie non trouvée avec l'ID: " + articleCreationDto.getCategoryId()));
            article.setCategory(category);
        }

        // Associer les services de blanchisserie
        if (articleCreationDto.getServicesIds() != null && !articleCreationDto.getServicesIds().isEmpty()) {
            Set<LaundryService> services = new HashSet<>();
            for (Long serviceId : articleCreationDto.getServicesIds()) {
                LaundryService service = laundryServiceRepository.findById(serviceId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Service non trouvé avec l'ID: " + serviceId));
                services.add(service);
            }
            article.setLaundryServices(services);
        }

        article = articleRepository.save(article);

        // Créer les instances d'articles
        article.createInstances();

        return articleRepository.save(article);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ArticleDto addLaundryServicesToArticle(Long articleId, List<Long> serviceIds) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));

        for (Long serviceId : serviceIds) {
            LaundryService service = laundryServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Service non trouvé avec l'ID: " + serviceId));
            article.addLaundryService(service);
        }

        article = articleRepository.save(article);
        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    public ArticleDto updateArticleDetails(Long articleId, ArticleCreationDto articleDetails) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));

        // Mettre à jour les détails
        if (articleDetails.getName() != null) {
            article.setName(articleDetails.getName());
        }
        if (articleDetails.getDescription() != null) {
            article.setDescription(articleDetails.getDescription());
        }
        if (articleDetails.getMaterial() != null) {
            article.setMaterial(articleDetails.getMaterial());
        }
        if (articleDetails.getColor() != null) {
            article.setColor(articleDetails.getColor());
        }
        if (articleDetails.getSize() != null) {
            article.setSize(articleDetails.getSize());
        }
        if (articleDetails.getBillingMode() != null) {
            article.setBillingMode(articleDetails.getBillingMode());
        }
        if (articleDetails.getEstimatedWeight() != null) {
            article.setEstimatedWeight(BigDecimal.valueOf(articleDetails.getEstimatedWeight()));
        }

        // Mettre à jour la catégorie si nécessaire
        if (articleDetails.getCategoryId() != null) {
            ArticleCategory category = articleCategoryRepository.findById(articleDetails.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Catégorie non trouvée avec l'ID: " + articleDetails.getCategoryId()));
            article.setCategory(category);
        }

        article = articleRepository.save(article);
        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    public ArticleDto removeLaundryServiceFromArticle(Long articleId, Long serviceId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));

        LaundryService service = laundryServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'ID: " + serviceId));

        article.removeLaundryService(service);
        article = articleRepository.save(article);
        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID: " + orderId));

        // Vérifier si la commande peut être supprimée
        if (order.getStatus() != null && 
            (order.getStatus().name().equals("IN_PRODUCTION") || 
             order.getStatus().name().equals("COMPLETED"))) {
            throw new InvalidOperationException(
                    "Impossible de supprimer une commande en production ou terminée");
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        // Vérifier que le client existe
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Client non trouvé avec l'ID: " + customerId);
        }

        List<Order> orders = orderRepository.findByCustomerIdOrderByDateDesc(customerId);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getArticlesByOrderId(Long orderId) {
        // Vérifier que la commande existe
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(
                    "Commande non trouvée avec l'ID: " + orderId);
        }

        List<Article> articles = articleRepository.findByOrderId(orderId);
        return articles.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleInstanceDto> getArticleInstancesByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));

        return article.getInstances().stream()
                .map(articleInstanceMapper::toDto)
                .collect(Collectors.toList());
    }

    // Méthodes auxiliaires privées

    private Article createArticleForOrder(ArticleCreationDto articleDto, Order order) {
        Article article = Article.builder()
                .name(articleDto.getName())
                .description(articleDto.getDescription())
                .quantity(articleDto.getQuantity())
                .material(articleDto.getMaterial())
                .color(articleDto.getColor())
                .size(articleDto.getSize())
                .billingMode(articleDto.getBillingMode())
                .estimatedWeight(articleDto.getEstimatedWeight() != null ? 
                        BigDecimal.valueOf(articleDto.getEstimatedWeight()) : null)
                .order(order)
                .build();

        // Associer la catégorie
        if (articleDto.getCategoryId() != null) {
            ArticleCategory category = articleCategoryRepository.findById(articleDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Catégorie non trouvée avec l'ID: " + articleDto.getCategoryId()));
            article.setCategory(category);
        }

        // Associer les services
        if (articleDto.getServicesIds() != null && !articleDto.getServicesIds().isEmpty()) {
            Set<LaundryService> services = new HashSet<>();
            for (Long serviceId : articleDto.getServicesIds()) {
                LaundryService service = laundryServiceRepository.findById(serviceId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Service non trouvé avec l'ID: " + serviceId));
                services.add(service);
            }
            article.setLaundryServices(services);
        }

        article = articleRepository.save(article);

        // Créer les instances
        article.createInstances();

        return articleRepository.save(article);
    }

    private void calculateOrderTotal(Order order) {
        // Utiliser le BillingService pour calculer le total
        BigDecimal total = billingService.calculateOrderTotalAmount(order);
        order.setTotalAmount(total);
    }
}
