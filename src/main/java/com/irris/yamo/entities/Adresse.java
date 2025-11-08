package com.irris.yamo.entities;



import com.irris.yamo.entities.enums.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AddressType addressType; // e.g., HOME, WORK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserYamo user;

    private Double latitude;
    private Double longitude;
    private String street;
    private String city;
    private String quarter;
    private String zip;
    private String country;

    private String note;

    /**
     * Retourne l'adresse complète formatée
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            sb.append(street);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (zip != null && !zip.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(zip);
        }
        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.length() > 0 ? sb.toString() : "Adresse non renseignée";
    }
    
    /**
     * Retourne une description courte de l'adresse
     */
    public String getShortAddress() {
        if (note != null && !note.isEmpty()) {
            return note;
        }
        return street != null ? street : "Adresse";
    }
}