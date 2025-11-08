package com.irris.yamo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "article_categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "is_active")
    private boolean isActive = true;
}
