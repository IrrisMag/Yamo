package com.irris.yamo.repositories;

import com.irris.yamo.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    // Recherche par identifiants uniques
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Optional<Driver> findByCniNumber(String cniNumber);
    Optional<Driver> findByVehicleNumber(String vehicleNumber);
    
    // Recherche chauffeurs disponibles
    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.isActive = true")
    List<Driver> findAvailableDrivers();
    
    // Recherche par type de véhicule
    List<Driver> findByVehicleType(String vehicleType);
    
    // Meilleurs chauffeurs (par note)
    @Query("SELECT d FROM Driver d WHERE d.isActive = true ORDER BY d.driverAverageRating DESC")
    List<Driver> findTopDriversByRating();
    
    // Chauffeurs actifs avec statistiques
    @Query("SELECT d FROM Driver d WHERE d.isActive = true AND d.totalDeliveries > :minDeliveries")
    List<Driver> findActiveDriversWithMinDeliveries(@Param("minDeliveries") Integer minDeliveries);
    
    // Compter chauffeurs disponibles par type véhicule
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.vehicleType = :vehicleType AND d.isAvailable = true")
    Long countAvailableByVehicleType(@Param("vehicleType") String vehicleType);
}
