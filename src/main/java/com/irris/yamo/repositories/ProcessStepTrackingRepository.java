package com.irris.yamo.repositories;

import com.irris.yamo.entities.ProcessStepTracking;
import com.irris.yamo.entities.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProcessStepTrackingRepository extends JpaRepository<ProcessStepTracking, Long> {
    
    List<ProcessStepTracking> findByStatus(ProcessingStatus status);
    
    // Chercher par operator.id et status
    @Query("SELECT pst FROM ProcessStepTracking pst WHERE pst.operator.id = :userId AND pst.status = :status")
    List<ProcessStepTracking> findByProcessedByIdAndStatus(@Param("userId") Long userId, @Param("status") ProcessingStatus status);
    
    @Query("SELECT pst FROM ProcessStepTracking pst WHERE pst.articleInstance.id = :instanceId ORDER BY pst.processStep.stepOrder")
    List<ProcessStepTracking> findByArticleInstanceIdOrderByStepOrder(@Param("instanceId") Long instanceId);
    
    @Query("SELECT pst FROM ProcessStepTracking pst WHERE pst.articleInstance.id = :instanceId ORDER BY pst.sequenceOrder ASC")
    List<ProcessStepTracking> findByArticleInstanceIdOrderBySequenceOrderAsc(@Param("instanceId") Long instanceId);
    
    @Query("SELECT pst FROM ProcessStepTracking pst WHERE pst.status = 'IN_PROGRESS' AND pst.startTime < :time")
    List<ProcessStepTracking> findStuckSteps(@Param("time") LocalDateTime time);
    
    @Query("SELECT pst FROM ProcessStepTracking pst WHERE pst.processStep.id = :stepId AND pst.status = :status")
    List<ProcessStepTracking> findByProcessStepIdAndStatus(@Param("stepId") Long stepId, @Param("status") ProcessingStatus status);
    
    @Query("SELECT pst FROM ProcessStepTracking pst WHERE pst.articleInstance.article.order.id = :orderId")
    List<ProcessStepTracking> findByOrderId(@Param("orderId") Long orderId);
}
