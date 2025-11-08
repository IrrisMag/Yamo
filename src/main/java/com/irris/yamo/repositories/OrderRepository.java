package com.irris.yamo.repositories;

import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByReference(String reference);
    
    List<Order> findByCustomer(UserYamo customer);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Order> findByCustomerIdOrderByDateDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.requiredCompletionDate < :date")
    List<Order> findLateOrders(@Param("status") OrderStatus status, @Param("date") LocalDateTime date);
    
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses")
    List<Order> findByStatusIn(@Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'IN_PRODUCTION' OR o.status = 'PRODUCTION_COMPLETED'")
    List<Order> findOrdersInProduction();
    
    @Query("SELECT o FROM Order o WHERE o.status = 'READY' OR o.status = 'DELIVERY_SCHEDULED'")
    List<Order> findOrdersReadyForDelivery();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
    
    // Compter les commandes par Customer (entité)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer = :customer")
    long countByCustomer(@Param("customer") UserYamo customer);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.customer.id = :customerId AND o.createdAt BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumTotalAmountByCustomerAndPeriod(@Param("customerId") Long customerId,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    java.math.BigDecimal sumAllTotalAmount();
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumTotalAmountBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<OrderStatus> statuses);
}
