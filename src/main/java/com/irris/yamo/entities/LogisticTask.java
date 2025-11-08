package com.irris.yamo.entities;

import com.irris.yamo.entities.enums.TaskStatus;
import com.irris.yamo.entities.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticTask {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalTime availableFrom;
    private LocalTime availableTo;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Adresse address;

    @Enumerated(EnumType.STRING)
    private TaskType type; // PICKUP ou DELIVERY

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String title;
    private String description;
    private String code;

    private boolean containsWeighting;

    private LocalDateTime createdAt;
    private LocalDateTime dueAt;
    private LocalDateTime arrivalTime;
    private LocalDateTime completedAt;

    // @ManyToOne
    // @JoinColumn(name = "route_id")
    // private DeliveryRoute route; // TODO: Créer classe DeliveryRoute si nécessaire

    @Column(name = "contact_name")
    private String contactName;
    
    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "signature_path")
    private String signaturePath;
    
    @ElementCollection
    @CollectionTable(name = "logistic_task_photos", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "photo_path")
    private List<String> photoPaths = new ArrayList<>();
    
    @Column(name = "sequence_order")
    private Integer sequenceOrder; // Ordre dans la tournée optimisée
    
//    public LogisticTask(TaskType type, LocalDate plannedAt, Order order, UserYamo driver, Adresse address,
//                       LocalTime availableFrom, LocalTime availableTo) {
//        this.type = type;
//        this.scheduledDate = plannedAt;
//        this.setDueAt(plannedAt.atTime(availableTo));
//        this.setOrder(order);
//        this.driver = driver;
//        this.address = address;
//        this.availableFrom = availableFrom;
//        this.availableTo = availableTo;
//
//        // Configuration du titre et de la description
//        this.setTitle(type == TaskType.PICKUP ? "Ramassage" : "Livraison" + " - " + order.getCode());
//        this.setDescription(String.format("%s pour %s à %s",
//            type == TaskType.PICKUP ? "Ramassage" : "Livraison",
//            order.getCustomer().getFullName(),
//            address.getFullAddress()));
//    }

    public void start() {
        if (status == TaskStatus.PENDING) {
            this.status = TaskStatus.IN_PROGRESS;
            this.arrivalTime = LocalDateTime.now();
        }
    }

    public void complete() {
        if (status == TaskStatus.IN_PROGRESS) {
            this.status = TaskStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();

        }
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    
    /**
     * Calcule la distance entre la position actuelle du livreur et l'adresse de la tâche
     * @param driverLatitude Latitude actuelle du livreur
     * @param driverLongitude Longitude actuelle du livreur
     * @return Distance en kilomètres, ou null si coordonnées invalides
     */
    @Transient
    public Double getDistanceFromDriver(Double driverLatitude, Double driverLongitude) {
        if (address == null || driverLatitude == null || driverLongitude == null) {
            return null;
        }
        if (address.getLatitude() == null || address.getLongitude() == null) {
            return null;
        }
        
        return GeoUtils.haversineDistance(
            driverLatitude, 
            driverLongitude,
            address.getLatitude(), 
            address.getLongitude()
        );
    }
    
    /**
     * Calcule la distance entre deux adresses
     * @param fromAddress Adresse de départ
     * @return Distance en kilomètres, ou null si coordonnées invalides
     */
    @Transient
    public Double getDistanceFrom(Adresse fromAddress) {
        if (fromAddress == null || address == null) {
            return null;
        }
        return GeoUtils.calculateDistance(fromAddress, address);
    }
}
