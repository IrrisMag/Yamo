package com.irris.yamo.repositories;


import com.irris.yamo.entities.Invoice;
import com.irris.yamo.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    Optional<Invoice> findByOrder(Order order);
    
    @Query("SELECT i FROM Invoice i WHERE i.order.customer.id = :customerId ORDER BY i.issueDate DESC")
    List<Invoice> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}
