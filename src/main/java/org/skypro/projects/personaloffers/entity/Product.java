package org.skypro.projects.personaloffers.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue
    private UUID id;
    
    @Transient
    private String type;
    
    @Column(nullable = false)
    private String name;

    public Product() {
    }

    public Product(UUID id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}