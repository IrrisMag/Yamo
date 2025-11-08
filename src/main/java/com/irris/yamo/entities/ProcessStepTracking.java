package com.irris.yamo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.irris.yamo.entities.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "process_step_tracking")
@Getter
@Setter
@NoArgsConstructor
public class ProcessStepTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_instance_id")
    @JsonIgnore
    private ArticleInstance articleInstance;

    @ManyToOne
    @JoinColumn(name = "process_step_id", nullable = false)
    private ProcessStep processStep;

    private boolean isCompleted = false;

    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt; // Alias pour startTime
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Alias pour endTime

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;



    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessingStatus status;


    @Column(name = "notes")
    private String notes;

    @Column(name = "issues_encountered")
    private String issuesEncountered;
    
    // Champs pour la gestion des retouches et opérateurs
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private UserYamo operator; // Opérateur assigné
    
    private LocalDateTime assignedAt; // Date d'assignation
    
    private Integer sequenceOrder; // Ordre dans la séquence
    
    private Boolean isRework = false; // Est-ce une retouche
    
    private String reworkReason; // Raison de la retouche
    
    private String operatorComment; // Commentaire de l'opérateur

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ProcessingStatus.PENDING;
        }
        if (isRework == null) {
            isRework = false;
        }
    }

    public void start() {
        if (this.startTime != null) {
            throw new IllegalStateException("Cette étape a déjà été démarrée");
        }
        this.startTime = LocalDateTime.now();
        this.status = ProcessingStatus.IN_PROGRESS;
    }

    public void complete() {
        this.isCompleted=true;
        this.endTime = LocalDateTime.now();
        this.status = ProcessingStatus.COMPLETED;
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    public void fail(String reason) {
        this.endTime = LocalDateTime.now();
        this.status = ProcessingStatus.FAILED;
        this.issuesEncountered = reason;
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    public boolean isCompleted() {
        return status == ProcessingStatus.COMPLETED;
    }

    public boolean isPending() {
        return status == ProcessingStatus.PENDING;
    }

    public boolean isInProgress() {
        return status == ProcessingStatus.IN_PROGRESS;
    }

    public boolean isFailed() {
        return status == ProcessingStatus.FAILED;
    }
}
