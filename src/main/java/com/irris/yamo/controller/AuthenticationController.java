package com.irris.yamo.controller;

import com.irris.yamo.dtos.auth.LoginRequestDto;
import com.irris.yamo.dtos.auth.LoginResponseDto;
import com.irris.yamo.dtos.auth.RegisterRequestDto;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Connexion avec username (ou email ou téléphone) + password
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Inscription d'un nouveau CLIENT
     */
    @PostMapping("/register/customer")
    public ResponseEntity<Map<String, Object>> registerCustomer(
            @RequestBody com.irris.yamo.dtos.creation.CustomerCreationDto customerData,
            @RequestParam String username,
            @RequestParam String password) {
        
        UserYamo user = authenticationService.registerCustomer(username, password, customerData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Client créé avec succès");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Inscription d'un nouveau CHAUFFEUR
     */
    @PostMapping("/register/driver")
    public ResponseEntity<Map<String, Object>> registerDriver(
            @RequestBody com.irris.yamo.dtos.creation.DriverCreationDto driverData,
            @RequestParam String username,
            @RequestParam String password) {
        
        UserYamo user = authenticationService.registerDriver(username, password, driverData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Chauffeur créé avec succès");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Inscription d'un nouvel OPÉRATEUR
     */
    @PostMapping("/register/operator")
    public ResponseEntity<Map<String, Object>> registerOperator(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String name = request.get("name");
        String email = request.get("email");
        String phoneNumber = request.get("phoneNumber");
        
        UserYamo user = authenticationService.registerOperator(username, password, name, email, phoneNumber);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Opérateur créé avec succès");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Inscription d'un nouvel ADMINISTRATEUR
     */
    @PostMapping("/register/admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String name = request.get("name");
        String email = request.get("email");
        String phoneNumber = request.get("phoneNumber");
        
        UserYamo user = authenticationService.registerAdmin(username, password, name, email, phoneNumber);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Administrateur créé avec succès");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authenticationService.logout(token);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Rafraîchir le token
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        LoginResponseDto response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Changer le mot de passe
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        authenticationService.changePassword(userId, oldPassword, newPassword);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe changé avec succès");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demander réinitialisation mot de passe
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authenticationService.requestPasswordReset(email);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Si l'email existe, un lien de réinitialisation a été envoyé");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Réinitialiser le mot de passe
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        authenticationService.resetPassword(token, newPassword);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe réinitialisé avec succès");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Valider un token
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = authenticationService.validateToken(token);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);
        
        return ResponseEntity.ok(response);
    }
}
