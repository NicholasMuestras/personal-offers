package org.skypro.projects.personaloffers.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.skypro.projects.personaloffers.entity.Term;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Converter
public class TermListConverter implements AttributeConverter<List<Term>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Term> terms) {
        try {
            return objectMapper.writeValueAsString(terms);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Term list to JSON", e);
        }
    }

    @Override
    public List<Term> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Term.class));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to Term list", e);
        }
    }
}