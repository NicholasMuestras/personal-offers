package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final List<RecommendationRuleSet> recommendationRuleSets;

    public ProductService(List<RecommendationRuleSet> recommendationRuleSet) {
        this.recommendationRuleSets = recommendationRuleSet;
    }

    public List<Product> getRecommendations(UUID userId) {
        List<Product> recommendedOffers = new ArrayList<>();

        for (RecommendationRuleSet ruleSet : this.recommendationRuleSets) {
            Optional<Product> offer = ruleSet.applyRules(userId);

            if (offer.isPresent()) {
                recommendedOffers.add(offer.get());
            }
        }

        return recommendedOffers;
    }
}
