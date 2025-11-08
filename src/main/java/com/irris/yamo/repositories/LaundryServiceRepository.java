package com.irris.yamo.repositories;

import com.irris.yamo.entities.LaundryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaundryServiceRepository extends JpaRepository<LaundryService, Long> {
    
    Optional<LaundryService> findByName(String name);
    
    List<LaundryService> findByIsAvailableTrue();
    
    @Query("SELECT ls FROM LaundryService ls WHERE ls.hasActivePromotion = true AND ls.isAvailable = true")
    List<LaundryService> findServicesWithActivePromotions();
    
    @Query("SELECT ls FROM LaundryService ls WHERE ls.pricePerKg IS NOT NULL")
    List<LaundryService> findServicesWithKgPricing();
    
    @Query("SELECT ls FROM LaundryService ls WHERE ls.pricePerPiece IS NOT NULL")
    List<LaundryService> findServicesWithPiecePricing();
}
