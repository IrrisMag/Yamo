package com.irris.yamo.repositories;

import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.Payment;
import com.irris.yamo.entities.enums.PaymentMethod;
import com.irris.yamo.entities.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByOrder(Order order);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByMethod(PaymentMethod method);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    @Query("SELECT p FROM Payment p WHERE p.order.customer.id = :customerId ORDER BY p.paymentDate DESC")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumCompletedPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p.method, SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' GROUP BY p.method")
    List<Object[]> sumPaymentsByMethod();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    java.math.BigDecimal sumAllCompletedPayments();
}
