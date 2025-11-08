package com.irris.yamo.entities;

import com.irris.yamo.entities.enums.CustomerSegment;
import com.irris.yamo.entities.enums.CustomerType;
import com.irris.yamo.entities.enums.Gender;
import com.irris.yamo.entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entité utilisateur unique consolidée
 * Remplace l'ancien système avec héritage (Customer, Driver, Operator, Admin)
 * Tous les attributs spécifiques sont nullable selon le rôle
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserYamo implements UserDetails {
    
    // ========================================
    // CHAMPS COMMUNS À TOUS LES UTILISATEURS
    // ========================================
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String username;

    @Column(unique = true, name = "phone_number")
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role; // CUSTOMER, DRIVER, OPERATOR, ADMIN

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Adresses de l'utilisateur
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Adresse> adresses = new ArrayList<>();

    // Champs d'audit
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    // Champs de sécurité
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked = true;

    @Column(name = "account_non_expired")
    private Boolean accountNonExpired = true;

    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired = true;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;




    // ========================================
    // LIFECYCLE HOOKS
    // ========================================
    
    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========================================
    // MÉTHODES HELPER
    // ========================================
    
    public String getFullName() {
        if (firstName == null || lastName == null) {
            return email;
        }
        
        if (gender == Gender.FEMALE) {
            return "Mme/Mlle " + firstName + " " + lastName;
        } else if (gender == Gender.MALE) {
            return "M. " + firstName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    public String getDisplayName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return email;
    }

    // Vérifications de rôle
    public boolean isCustomer() {
        return role == Role.ROLE_CUSTOMER;
    }

    public boolean isDriver() {
        return role == Role.ROLE_DRIVER;
    }

    public boolean isOperator() {
        return role == Role.ROLE_OPERATOR;
    }

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }

    // ========================================
    // IMPLÉMENTATION UserDetails
    // ========================================
    


    @Override
    public String getUsername() {
        return username != null ? username : email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    // ========================================
    // ENUMS INTERNES
    // ========================================
    
    public enum OperatorType {
        RECEPTIONIST("Réceptionniste"),
        PRODUCTION("Agent de production"),
        ACCOUNTANT("Comptable"),
        QUALITY_CONTROL("Contrôle qualité"),
        WAREHOUSE("Magasinier"),
        GENERAL("Opérateur général");

        private final String displayName;

        OperatorType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AdminLevel {
        STANDARD("Administrateur standard"),
        SENIOR("Administrateur senior"),
        SUPER_ADMIN("Super administrateur");

        private final String displayName;

        AdminLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
