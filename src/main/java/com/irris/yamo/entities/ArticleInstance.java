package com.irris.yamo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.irris.yamo.entities.enums.InstanceStatus;
import com.irris.yamo.entities.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class ArticleInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String size;
    private String color;
    private String material; // Matière (ex: "Coton", "Soie", "Polyester", "Laine")
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @JsonIgnore
    private Article article;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProcessStepTracking> trackings = new HashSet<>();

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstanceStatus status = InstanceStatus.PENDING;
    

    
    private LocalDateTime qualityCheckDate;
    
    private Long qualityCheckedBy; // ID de l'opérateur qui a fait le contrôle
    
    private String qualityIssueDescription; // Description du problème si échec
    
    private Boolean requiresRework = false; // Nécessite une retouche
    
    private Integer reworkCount = 0; // Nombre de retouches effectuées

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (status == null) {
            status = InstanceStatus.PENDING;
        }
    }

    public boolean isCompleted() {
        if (trackings == null || trackings.isEmpty()) return false;
        boolean completed = trackings.stream().allMatch(ProcessStepTracking::isCompleted);
        
        if (completed && completedDate == null) {
            completedDate = LocalDateTime.now();
            status = InstanceStatus.COMPLETED;
        }
        
        return completed;
    }

    public double getProgress() {
        if (trackings == null || trackings.isEmpty()) return 0.0;
        long completedSteps = trackings.stream()
                .filter(ProcessStepTracking::isCompleted)
                .count();
        return (double) completedSteps / trackings.size() * 100;
    }
    

}