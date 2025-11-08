package com.irris.yamo.entities;

import com.irris.yamo.entities.enums.CustomerSegment;
import com.irris.yamo.entities.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends UserYamo{


    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type")
    private CustomerType customerType; // INDIVIDUAL, ENTERPRISE, etc.

    @Column(name = "company_name")
    private String companyName; // Pour les entreprises

    @Column(unique = true)
    private String nui; // Numéro Unique d'Identification

    @Column(unique = true)
    private String rccm; // Registre du Commerce

    @Column(name = "contact_person")
    private String contactPerson; // Personne de contact pour entreprises

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_segment")
    private CustomerSegment customerSegment; // STANDARD, REGULAR, VIP, INACTIVE

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "customer_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Column(name = "customer_credit")
    private Double customerCredit = 0.0;

    private Double customerDebt = 0.0;

    private Double customerBalance = 0.0;

    @Column(name = "custom_discount")
    private Double customDiscount = 0.0; // Remise personnalisée %

    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_spent")
    private Double totalSpent = 0.0;

    @Column(name = "monthly_average")
    private Double monthlyAverage = 0.0;

    @Column(name = "is_company")
    private Boolean isCompany = false;

    // Préférences de communication CUSTOMER
    @Column(name = "whatsapp_notifications")
    private Boolean whatsappNotifications = true;

    @Column(name = "email_notifications")
    private Boolean emailNotifications = false;

    @Column(name = "sms_notifications")
    private Boolean smsNotifications = false;

    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;

    @Column(name = "fcm_token")
    private String fcmToken; // Token FCM pour push notifications
}
