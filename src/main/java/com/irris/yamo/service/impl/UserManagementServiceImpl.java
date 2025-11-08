package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.CustomerDto;
import com.irris.yamo.dtos.DriverDto;
import com.irris.yamo.entities.Customer;
import com.irris.yamo.entities.Driver;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.Role;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.CustomerMapper;
import com.irris.yamo.mapper.DriverMapper;
import com.irris.yamo.repositories.CustomerRepository;
import com.irris.yamo.repositories.DriverRepository;
import com.irris.yamo.repositories.LogisticTaskRepository;
import com.irris.yamo.repositories.UserRepository;
import com.irris.yamo.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final LogisticTaskRepository logisticTaskRepository;
    private final CustomerMapper customerMapper;
    private final DriverMapper driverMapper;

    // ========== CRUD Utilisateurs ==========

    @Override
    @Transactional(readOnly = true)
    public UserYamo getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserYamo> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserYamo> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserYamo> getActiveUsers() {
        return userRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public UserYamo findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserYamo findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec le téléphone: " + phoneNumber));
    }

    // ========== Activation / Désactivation ==========

    @Override
    @Transactional
    public void activateUser(Long userId) {
        UserYamo user = getUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        UserYamo user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserYamo user = getUserById(userId);
        
        // Soft delete
        user.setIsActive(false);
        userRepository.save(user);
    }

    // ========== Gestion Drivers ==========

    @Override
    @Transactional(readOnly = true)
    public List<DriverDto> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(driverMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverDto> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers().stream()
                .map(driverMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setDriverAvailable(Long driverId, boolean available) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur non trouvé avec l'ID: " + driverId));
        
        driver.setIsAvailable(available);
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void assignTaskToDriver(Long driverId, Long taskId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur non trouvé"));
        
        if (!driver.getIsAvailable()) {
            throw new InvalidOperationException("Ce chauffeur n'est pas disponible");
        }
        
        // L'affectation de la tâche se fait via LogisticService
        // Cette méthode vérifie juste la disponibilité
    }

    // ========== Gestion Customers ==========

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getVipCustomers() {
        return customerRepository.findAll().stream()
                .filter(customer -> com.irris.yamo.entities.enums.CustomerSegment.VIP.equals(customer.getCustomerSegment()))
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCustomerSegment(Long customerId, String segment) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + customerId));
        
        customer.setCustomerSegment(com.irris.yamo.entities.enums.CustomerSegment.valueOf(segment));
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void addCustomerCredit(Long customerId, Double amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        
        if (amount < 0) {
            throw new InvalidOperationException("Le montant doit être positif");
        }
        
        Double currentCredit = customer.getCustomerCredit() != null ? customer.getCustomerCredit() : 0.0;
        customer.setCustomerCredit(currentCredit + amount);
        customerRepository.save(customer);
    }

    // ========== Statistiques ==========

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByIsActiveTrue());
        stats.put("totalCustomers", userRepository.countByRole(Role.ROLE_CUSTOMER));
        stats.put("totalDrivers", userRepository.countByRole(Role.ROLE_DRIVER));
        stats.put("totalOperators", userRepository.countByRole(Role.ROLE_OPERATOR));
        stats.put("totalAdmins", userRepository.countByRole(Role.ROLE_ADMIN));
        stats.put("availableDrivers", driverRepository.findAvailableDrivers().size());
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }
}
