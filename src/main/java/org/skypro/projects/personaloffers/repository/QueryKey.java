package org.skypro.projects.personaloffers.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс для представления ключа кеша запросов
 */
public class QueryKey implements Serializable {
    
    private final String queryName;
    private final UUID userId;
    private final List<String> arguments;
    
    public QueryKey(String queryName, UUID userId, List<String> arguments) {
        this.queryName = queryName;
        this.userId = userId;
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryKey queryKey = (QueryKey) o;
        return Objects.equals(queryName, queryKey.queryName) && 
               Objects.equals(userId, queryKey.userId) && 
               Objects.equals(arguments, queryKey.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryName, userId, arguments);
    }
}