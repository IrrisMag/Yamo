package com.irris.yamo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "process_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder = 0;

    @Column(name = "estimated_duration")
    private Integer estimatedDurationMinutes;

    @Column(name = "requires_quality_check")
    private Boolean requiresQualityCheck = false;

    @Column(name = "is_optional")
    private Boolean isOptional = false;

    @Column(name = "is_active")
    private Boolean isActive = true;


}
