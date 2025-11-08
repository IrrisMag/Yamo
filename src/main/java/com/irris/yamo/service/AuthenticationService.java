package com.irris.yamo.service;

import com.irris.yamo.dtos.auth.LoginRequestDto;
import com.irris.yamo.dtos.auth.LoginResponseDto;
import com.irris.yamo.dtos.auth.RegisterRequestDto;
import com.irris.yamo.entities.UserYamo;

public interface AuthenticationService {

    /**
     * Connexion avec username et password
     */
    LoginResponseDto login(LoginRequestDto request);

    /**
     * Inscription d'un nouveau client
     */
    UserYamo registerCustomer(String username, String password, com.irris.yamo.dtos.creation.CustomerCreationDto customerData);

    /**
     * Inscription d'un nouveau chauffeur
     */
    UserYamo registerDriver(String username, String password, com.irris.yamo.dtos.creation.DriverCreationDto driverData);

    /**
     * Inscription d'un nouvel opérateur
     */
    UserYamo registerOperator(String username, String password, String name, String email, String phoneNumber);

    /**
     * Inscription d'un nouvel administrateur
     */
    UserYamo registerAdmin(String username, String password, String name, String email, String phoneNumber);

    /**
     * Déconnexion
     */
    void logout(String token);

    /**
     * Valider un token JWT
     */
    boolean validateToken(String token);

    /**
     * Rafraîchir un token
     */
    LoginResponseDto refreshToken(String refreshToken);

    /**
     * Obtenir l'utilisateur depuis le token
     */
    UserYamo getUserFromToken(String token);

    /**
     * Changer le mot de passe
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Réinitialiser mot de passe (envoi email)
     */
    void requestPasswordReset(String email);

    /**
     * Confirmer réinitialisation mot de passe
     */
    void resetPassword(String token, String newPassword);
}
