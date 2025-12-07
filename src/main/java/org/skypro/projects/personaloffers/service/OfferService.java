package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Offer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OfferService {

    private final List<RecommendationRuleSet> recommendationRuleSets;

    public OfferService(List<RecommendationRuleSet> recommendationRuleSet) {
        this.recommendationRuleSets = recommendationRuleSet;
    }

    public List<Offer> getRecommendations(UUID userId) {
        List<Offer> recommendedOffers = new ArrayList<>();

        for (RecommendationRuleSet ruleSet : this.recommendationRuleSets) {
            Optional<Offer> offer = ruleSet.applyRules(userId);

            if (offer.isPresent()) {
                recommendedOffers.add(offer.get());
            }
        }

        return recommendedOffers;
    }
}
