package org.skypro.projects.personaloffers.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Data
public class Term {
    private String query;
    private List<String> arguments = new ArrayList<>();
    private boolean negate;

    public Term() {
    }

    @JsonCreator
    public Term(@JsonProperty("query") String query, @JsonProperty("arguments") List<String> arguments, @JsonProperty("negate") boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }
}