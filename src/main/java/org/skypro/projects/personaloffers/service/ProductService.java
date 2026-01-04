package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductService {

    @Autowired
    private final RuleService dynamicRuleService;

    private final List<RecommendationRuleSet> recommendationRuleSets;

    public ProductService(RuleService dynamicRuleService, List<RecommendationRuleSet> recommendationRuleSet) {
        this.dynamicRuleService = dynamicRuleService;
        // static rules loading
        this.recommendationRuleSets = recommendationRuleSet;
        // dynamic rules loading
        List<DynamicRule> rules = dynamicRuleService.getAll();

        if (!rules.isEmpty()) {
            this.recommendationRuleSets.addAll(rules);
        }
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
