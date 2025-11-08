package com.irris.yamo.entities;

import com.irris.yamo.entities.enums.ComplaintStatus;
import com.irris.yamo.entities.enums.ComplaintType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserYamo customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private ComplaintType type;

    private String description;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private UserYamo assignedTo;

    private String resolutionNotes;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ComplaintStatus.OPEN;
    }
}

