package org.skypro.projects.personaloffers.dto;

import java.util.UUID;

public class OfferDto {
    private UUID id;
    private String name;
    private String text;

    public OfferDto() {
    }

    public OfferDto(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
