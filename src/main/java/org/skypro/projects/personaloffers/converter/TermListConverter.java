package org.skypro.projects.personaloffers.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.skypro.projects.personaloffers.entity.Term;

import java.util.List;

@Converter(autoApply = true)
public class TermListConverter implements AttributeConverter<List<Term>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Term> terms) {
        if (terms == null || terms.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(terms);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Term list to JSON", e);
        }
    }

    @Override
    public List<Term> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty() || "null".equals(json)) {
            return List.of();
        }
        try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Term.class);

            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to Term list", e);
        }
    }
}
