package com.irris.yamo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class LaundryService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // Prix pour chaque mode de facturation
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerPiece;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    
    @ManyToMany
    @JoinTable(
        name = "service_process_steps",
        joinColumns = @JoinColumn(name = "service_id"),
        inverseJoinColumns = @JoinColumn(name = "step_id")
    )
    @JsonIgnore
    private Set<ProcessStep> steps = new HashSet<>();

    // Durée estimée totale en minutes
    private Integer estimatedDurationMinutes;

    // Indique si le service est actuellement disponible
    private Boolean isAvailable = true;

    // Limites pour le service
    @Column(precision = 10, scale = 3)
    private BigDecimal minimumWeight;
    
    @Column(precision = 10, scale = 3)
    private BigDecimal maximumWeight;
    
    private Integer minimumPieces;
    private Integer maximumPieces;

    // Pour les promotions et remises
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    
    private Boolean hasActivePromotion;

    /**
     * Ajoute une étape au service
     */
    public void addProcessStep(ProcessStep step) {
        if (steps == null) {
            steps = new HashSet<>();
        }
        steps.add(step);
    }
    
    /**
     * Retire une étape du service
     */
    public void removeProcessStep(ProcessStep step) {
        if (steps != null) {
            steps.remove(step);
        }
    }
    
    /**
     * Vérifie si le service est valide
     */
    public boolean isValid() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (steps == null || steps.isEmpty()) {
            return false;
        }
        if (pricePerKg == null && pricePerPiece == null ) {
            return false;
        }
        return true;
    }
}
