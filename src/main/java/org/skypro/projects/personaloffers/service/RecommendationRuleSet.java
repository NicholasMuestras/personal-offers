package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<Product> applyRules(UUID userId);
}
