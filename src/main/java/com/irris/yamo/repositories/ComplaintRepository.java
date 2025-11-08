package com.irris.yamo.repositories;

import com.irris.yamo.entities.Complaint;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.ComplaintStatus;
import com.irris.yamo.entities.enums.ComplaintType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByCustomer(UserYamo customer);
    
    List<Complaint> findByOrder(Order order);
    
    List<Complaint> findByStatus(ComplaintStatus status);
    
    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);
    
    List<Complaint> findByType(ComplaintType type);
    
    @Query("SELECT c FROM Complaint c WHERE c.status = 'OPEN' OR c.status = 'IN_PROGRESS'")
    List<Complaint> findOpenComplaints();
    
    @Query("SELECT c FROM Complaint c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Complaint> findComplaintsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c.type, COUNT(c) FROM Complaint c GROUP BY c.type")
    List<Object[]> countByType();
}
