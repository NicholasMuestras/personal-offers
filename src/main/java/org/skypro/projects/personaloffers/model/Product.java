package org.skypro.projects.personaloffers.model;

import java.util.UUID;

public class Product {
    private UUID id;
    private String type;
    private String name;
    private String description;

    public Product() {
    }

    public Product(UUID id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Product(UUID id, String type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
