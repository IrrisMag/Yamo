package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.auth.LoginRequestDto;
import com.irris.yamo.dtos.auth.LoginResponseDto;
import com.irris.yamo.dtos.auth.RegisterRequestDto;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.Role;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.UserRepository;
import com.irris.yamo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final com.irris.yamo.repositories.CustomerRepository customerRepository;
    private final com.irris.yamo.repositories.DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final com.irris.yamo.security.JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        log.info("Tentative de connexion pour: {}", request.getUsername());

        // Authentification
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Récupérer l'utilisateur
        UserYamo user = findUserByUsernameOrEmailOrPhone(request.getUsername());

        if (!user.getIsActive()) {
            throw new InvalidOperationException("Compte désactivé");
        }

        // Générer tokens JWT
        String token = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("Connexion réussie pour: {} ({})", user.getUsername(), user.getRole());

        return LoginResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .expiresIn(3600L) // 1 heure
                .build();
    }

    @Override
    @Transactional
    public UserYamo registerCustomer(String username, String password, com.irris.yamo.dtos.creation.CustomerCreationDto customerData) {
        log.info("Tentative d'inscription client pour: {}", username);

        if (customerData == null) {
            throw new InvalidOperationException("Données client requises");
        }

        // Vérifications unicité
        validateUniqueness(username, customerData.getEmail(), customerData.getPhoneNumber());

        // Créer Customer
        com.irris.yamo.entities.Customer customer = createCustomerEntity(customerData);
        
        // Champs communs
        setCommonFields(customer, username, password, com.irris.yamo.entities.enums.Role.ROLE_CUSTOMER);

        // Utiliser CustomerRepository (typage fort, pas de cast)
        com.irris.yamo.entities.Customer savedCustomer = customerRepository.save(customer);
        log.info("Client créé: {} ({})", savedCustomer.getUsername(), savedCustomer.getCustomerType());

        return savedCustomer;
    }

    @Override
    @Transactional
    public UserYamo registerDriver(String username, String password, com.irris.yamo.dtos.creation.DriverCreationDto driverData) {
        log.info("Tentative d'inscription chauffeur pour: {}", username);

        if (driverData == null) {
            throw new InvalidOperationException("Données chauffeur requises");
        }

        // Email généré automatiquement pour driver
        String generatedEmail = driverData.getName().toLowerCase().replace(" ", ".") + "@yamo-driver.com";

        // Vérifications unicité
        validateUniqueness(username, generatedEmail, driverData.getPhoneNumber());

        // Créer Driver
        com.irris.yamo.entities.Driver driver = createDriverEntity(driverData);
        
        // Champs communs
        setCommonFields(driver, username, password, com.irris.yamo.entities.enums.Role.ROLE_DRIVER);

        // Utiliser DriverRepository (typage fort, pas de cast)
        com.irris.yamo.entities.Driver savedDriver = driverRepository.save(driver);
        log.info("Chauffeur créé: {} (Permis: {})", savedDriver.getUsername(), savedDriver.getLicenseNumber());

        return savedDriver;
    }

    @Override
    @Transactional
    public UserYamo registerOperator(String username, String password, String name, String email, String phoneNumber) {
        log.info("Tentative d'inscription opérateur pour: {}", username);

        // Vérifications unicité
        validateUniqueness(username, email, phoneNumber);

        // Créer Operator
        com.irris.yamo.entities.Operator operator = new com.irris.yamo.entities.Operator();
        operator.setEmail(email);
        operator.setPhoneNumber(phoneNumber);
        
        String[] nameParts = parseFullName(name);
        operator.setFirstName(nameParts[0]);
        operator.setLastName(nameParts[1]);
        
        // Champs communs
        setCommonFields(operator, username, password, com.irris.yamo.entities.enums.Role.ROLE_OPERATOR);

        com.irris.yamo.entities.Operator savedOperator = (com.irris.yamo.entities.Operator) userRepository.save(operator);
        log.info("Opérateur créé: {}", savedOperator.getUsername());

        return savedOperator;
    }

    @Override
    @Transactional
    public UserYamo registerAdmin(String username, String password, String name, String email, String phoneNumber) {
        log.info("Tentative d'inscription admin pour: {}", username);

        // Vérifications unicité
        validateUniqueness(username, email, phoneNumber);

        // Créer Admin
        com.irris.yamo.entities.Admin admin = new com.irris.yamo.entities.Admin();
        admin.setEmail(email);
        admin.setPhoneNumber(phoneNumber);
        
        String[] nameParts = parseFullName(name);
        admin.setFirstName(nameParts[0]);
        admin.setLastName(nameParts[1]);
        
        // Champs communs
        setCommonFields(admin, username, password, com.irris.yamo.entities.enums.Role.ROLE_ADMIN);

        com.irris.yamo.entities.Admin savedAdmin = (com.irris.yamo.entities.Admin) userRepository.save(admin);
        log.info("Admin créé: {}", savedAdmin.getUsername());

        return savedAdmin;
    }

    /**
     * Valide l'unicité username, email et téléphone
     */
    private void validateUniqueness(String username, String email, String phoneNumber) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new InvalidOperationException("Username déjà utilisé");
        }

        if (email != null && userRepository.findByEmail(email).isPresent()) {
            throw new InvalidOperationException("Email déjà utilisé");
        }

        if (phoneNumber != null && userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new InvalidOperationException("Numéro de téléphone déjà utilisé");
        }
    }

    /**
     * Définit les champs communs à tous les utilisateurs
     */
    private void setCommonFields(UserYamo user, String username, String password, com.irris.yamo.entities.enums.Role role) {
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
    }

    /**
     * Crée une entité Customer avec CustomerCreationDto
     */
    private com.irris.yamo.entities.Customer createCustomerEntity(com.irris.yamo.dtos.creation.CustomerCreationDto customerData) {
        com.irris.yamo.entities.Customer customer = new com.irris.yamo.entities.Customer();
        
        // Informations de base
        customer.setEmail(customerData.getEmail());
        customer.setPhoneNumber(customerData.getPhoneNumber());
        
        // Parser le nom complet
        String[] nameParts = parseFullName(customerData.getName());
        customer.setFirstName(nameParts[0]);
        customer.setLastName(nameParts[1]);
        
        // Gender
        if (customerData.getGender() != null) {
            try {
                customer.setGender(com.irris.yamo.entities.enums.Gender.valueOf(customerData.getGender().toUpperCase()));
            } catch (Exception e) {
                customer.setGender(null);
            }
        }
        
        // Type de client
        if ("BUSINESS".equalsIgnoreCase(customerData.getCustomerType()) || 
            "COMPANY".equalsIgnoreCase(customerData.getCustomerType())) {
            customer.setCustomerType(com.irris.yamo.entities.enums.CustomerType.BUSINESS);
            customer.setCompanyName(customerData.getName());
            customer.setRccm(customerData.getRccm());
            customer.setCustomDiscount(5.0); // 5% remise entreprise
            customer.setIsCompany(true);
        } else {
            customer.setCustomerType(com.irris.yamo.entities.enums.CustomerType.INDIVIDUAL);
            customer.setIsCompany(false);
        }
        
        // NUI (Numéro Unique d'Identification)
        customer.setNui(customerData.getNui());
        
        // Initialisation valeurs par défaut
        customer.setCustomerSegment(com.irris.yamo.entities.enums.CustomerSegment.STANDARD);
        customer.setCustomerCredit(0.0);
        customer.setCustomerBalance(0.0);
        customer.setCustomerDebt(0.0);
        customer.setTotalOrders(0);
        customer.setTotalSpent(0.0);
        customer.setMonthlyAverage(0.0);
        customer.setRegistrationDate(LocalDateTime.now());
        
        log.info("Client créé: {} ({})", customer.getUsername(), customer.getCustomerType());
        return customer;
    }

    /**
     * Crée une entité Driver avec DriverCreationDto
     */
    private com.irris.yamo.entities.Driver createDriverEntity(com.irris.yamo.dtos.creation.DriverCreationDto driverData) {
        com.irris.yamo.entities.Driver driver = new com.irris.yamo.entities.Driver();
        
        // Informations de base
        driver.setPhoneNumber(driverData.getPhoneNumber());
        
        // Parser le nom complet
        String[] nameParts = parseFullName(driverData.getName());
        driver.setFirstName(nameParts[0]);
        driver.setLastName(nameParts[1]);
        
        // Email généré si non fourni
        driver.setEmail(driverData.getName().toLowerCase().replace(" ", ".") + "@yamo-driver.com");
        
        // Informations Driver
        driver.setLicenseNumber(driverData.getLicenseNumber());
        driver.setVehicleType(driverData.getVehicleType());
        driver.setVehicleNumber(driverData.getVehiclePlate());
        driver.setVehicleColor(driverData.getVehicleColor());
        driver.setVehicleBrand(driverData.getVehicleBrand());
        driver.setVehicleChassisNumber(driverData.getVehicleChassisNumber());
        driver.setCniNumber(driverData.getCniNumber());
        driver.setProfileImageUrl(driverData.getImageUrl());
        
        // Initialisation valeurs par défaut
        driver.setIsAvailable(true);
        driver.setTotalDeliveries(0);
        driver.setTotalPickups(0);
        driver.setDriverAverageRating(0.0);
        
        log.info("Chauffeur créé: {} (Permis: {})", driver.getUsername(), driver.getLicenseNumber());
        return driver;
    }

    /**
     * Parse un nom complet en firstName et lastName
     */
    private String[] parseFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"", ""};
        }
        
        String[] parts = fullName.trim().split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], ""};
        }
        return parts;
    }

    @Override
    public void logout(String token) {
        // TODO: Ajouter token à une blacklist (Redis recommandé)
        log.info("Déconnexion - Token invalidé");
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    @Transactional
    public LoginResponseDto refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidOperationException("Refresh token invalide ou expiré");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        UserYamo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!user.getIsActive()) {
            throw new InvalidOperationException("Compte désactivé");
        }

        // Générer nouveau token
        String newToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        return LoginResponseDto.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .expiresIn(3600L)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserYamo getUserFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidOperationException("Token invalide");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserYamo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidOperationException("Ancien mot de passe incorrect");
        }

        // Changer le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Mot de passe changé pour l'utilisateur: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        Optional<UserYamo> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            // Ne pas révéler si l'email existe ou non (sécurité)
            log.warn("Demande de reset password pour email inexistant: {}", email);
            return;
        }

        UserYamo user = userOpt.get();
        
        // Générer token de réinitialisation
        String resetToken = UUID.randomUUID().toString();
        
        // TODO: Sauvegarder token avec expiration (Redis ou table)
        // TODO: Envoyer email avec lien de réinitialisation
        
        log.info("Token de réinitialisation généré pour: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // TODO: Vérifier token de réinitialisation
        // TODO: Récupérer userId depuis token
        // TODO: Vérifier expiration
        
        // UserYamo user = userRepository.findById(userId).orElseThrow();
        // user.setPassword(passwordEncoder.encode(newPassword));
        // userRepository.save(user);
        
        throw new InvalidOperationException("Reset password non implémenté - TODO");
    }

    // ========== Méthodes Utilitaires ==========

    /**
     * Trouve un utilisateur par username, email OU téléphone
     */
    private UserYamo findUserByUsernameOrEmailOrPhone(String identifier) {
        // Essayer par username
        Optional<UserYamo> user = userRepository.findByUsername(identifier);
        if (user.isPresent()) {
            return user.get();
        }

        // Essayer par email
        user = userRepository.findByEmail(identifier);
        if (user.isPresent()) {
            return user.get();
        }

        // Essayer par téléphone
        user = userRepository.findByPhoneNumber(identifier);
        if (user.isPresent()) {
            return user.get();
        }

        throw new ResourceNotFoundException("Utilisateur non trouvé: " + identifier);
    }
}
