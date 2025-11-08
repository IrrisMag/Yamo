package com.irris.yamo.repositories;

import com.irris.yamo.entities.ProcessStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessStepRepository extends JpaRepository<ProcessStep, Long> {
    List<ProcessStep> findByIsActiveTrue();
    List<ProcessStep> findByIsActiveTrueOrderByStepOrder();
    List<ProcessStep> findAllByOrderByStepOrder();
}
