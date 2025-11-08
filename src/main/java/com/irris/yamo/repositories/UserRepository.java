package com.irris.yamo.repositories;

import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository unifié pour tous les utilisateurs
 */
@Repository
public interface UserRepository extends JpaRepository<UserYamo, Long> {
    
    // Recherche basique
    Optional<UserYamo> findByEmail(String email);
    Optional<UserYamo> findByUsername(String username);
    Optional<UserYamo> findByPhoneNumber(String phoneNumber);
    
    // Recherche par rôle
    List<UserYamo> findByRole(Role role);
    List<UserYamo> findByRoleAndIsActiveTrue(Role role);
    
    // Comptage
    long countByRole(Role role);
    long countByIsActiveTrue();
    
    // Recherche active
    @Query("SELECT u FROM UserYamo u WHERE u.isActive = true")
    List<UserYamo> findAllActive();
    
    @Query("SELECT u FROM UserYamo u WHERE u.role = :role AND u.isActive = true")
    List<UserYamo> findActiveByRole(Role role);
    
    // Existence
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
