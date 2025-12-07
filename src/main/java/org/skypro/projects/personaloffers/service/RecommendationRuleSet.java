package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Offer;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<Offer> applyRules(UUID userId);
}
