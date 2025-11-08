package com.irris.yamo.service;

import com.irris.yamo.dtos.CustomerDto;
import com.irris.yamo.dtos.DriverDto;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.Role;

import java.util.List;
import java.util.Map;

public interface UserManagementService {

    // ========== CRUD Utilisateurs ==========
    
    /**
     * Récupère un utilisateur par ID
     */
    UserYamo getUserById(Long id);

    /**
     * Récupère tous les utilisateurs
     */
    List<UserYamo> getAllUsers();

    /**
     * Récupère les utilisateurs par rôle
     */
    List<UserYamo> getUsersByRole(Role role);

    /**
     * Récupère les utilisateurs actifs
     */
    List<UserYamo> getActiveUsers();

    /**
     * Recherche utilisateur par email
     */
    UserYamo findByEmail(String email);

    /**
     * Recherche utilisateur par téléphone
     */
    UserYamo findByPhoneNumber(String phoneNumber);

    // ========== Activation / Désactivation ==========

    /**
     * Active un utilisateur
     */
    void activateUser(Long userId);

    /**
     * Désactive un utilisateur
     */
    void deactivateUser(Long userId);

    /**
     * Supprime un utilisateur (soft delete)
     */
    void deleteUser(Long userId);

    // ========== Gestion Drivers ==========

    /**
     * Récupère tous les chauffeurs
     */
    List<DriverDto> getAllDrivers();

    /**
     * Récupère les chauffeurs disponibles
     */
    List<DriverDto> getAvailableDrivers();

    /**
     * Marque un chauffeur comme disponible
     */
    void setDriverAvailable(Long driverId, boolean available);

    /**
     * Assigne une tâche à un chauffeur
     */
    void assignTaskToDriver(Long driverId, Long taskId);

    // ========== Gestion Customers ==========

    /**
     * Récupère tous les clients
     */
    List<CustomerDto> getAllCustomers();

    /**
     * Récupère les clients VIP
     */
    List<CustomerDto> getVipCustomers();

    /**
     * Met à jour le segment d'un client
     */
    void updateCustomerSegment(Long customerId, String segment);

    /**
     * Ajoute du crédit à un client
     */
    void addCustomerCredit(Long customerId, Double amount);

    // ========== Statistiques ==========

    /**
     * Statistiques globales des utilisateurs
     */
    Map<String, Object> getUserStatistics();

    /**
     * Compte les utilisateurs par rôle
     */
    Long countUsersByRole(Role role);

    /**
     * Compte les utilisateurs actifs
     */
    Long countActiveUsers();
}
