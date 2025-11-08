package com.irris.yamo.controller;

import com.irris.yamo.dtos.CustomerDto;
import com.irris.yamo.dtos.DriverDto;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.Role;
import com.irris.yamo.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    // ========== CRUD Utilisateurs ==========

    @GetMapping("/{id}")
    public ResponseEntity<UserYamo> getUserById(@PathVariable Long id) {
        UserYamo user = userManagementService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserYamo>> getAllUsers() {
        List<UserYamo> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserYamo>> getUsersByRole(@PathVariable Role role) {
        List<UserYamo> users = userManagementService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserYamo>> getActiveUsers() {
        List<UserYamo> users = userManagementService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserYamo> findByEmail(@PathVariable String email) {
        UserYamo user = userManagementService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<UserYamo> findByPhoneNumber(@PathVariable String phoneNumber) {
        UserYamo user = userManagementService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(user);
    }

    // ========== Activation / Désactivation ==========

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userManagementService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userManagementService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ========== Gestion Drivers ==========

    @GetMapping("/drivers")
    public ResponseEntity<List<DriverDto>> getAllDrivers() {
        List<DriverDto> drivers = userManagementService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<List<DriverDto>> getAvailableDrivers() {
        List<DriverDto> drivers = userManagementService.getAvailableDrivers();
        return ResponseEntity.ok(drivers);
    }

    @PostMapping("/drivers/{id}/availability")
    public ResponseEntity<Void> setDriverAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        userManagementService.setDriverAvailable(id, available);
        return ResponseEntity.ok().build();
    }

    // ========== Gestion Customers ==========

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = userManagementService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers/vip")
    public ResponseEntity<List<CustomerDto>> getVipCustomers() {
        List<CustomerDto> customers = userManagementService.getVipCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/customers/{id}/segment")
    public ResponseEntity<Void> updateCustomerSegment(
            @PathVariable Long id,
            @RequestParam String segment) {
        userManagementService.updateCustomerSegment(id, segment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/customers/{id}/credit")
    public ResponseEntity<Void> addCustomerCredit(
            @PathVariable Long id,
            @RequestParam Double amount) {
        userManagementService.addCustomerCredit(id, amount);
        return ResponseEntity.ok().build();
    }

    // ========== Statistiques ==========

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = userManagementService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable Role role) {
        Long count = userManagementService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveUsers() {
        Long count = userManagementService.countActiveUsers();
        return ResponseEntity.ok(count);
    }
}
