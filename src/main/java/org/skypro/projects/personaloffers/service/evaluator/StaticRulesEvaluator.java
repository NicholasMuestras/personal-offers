package org.skypro.projects.personaloffers.service.evaluator;

import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.service.RecommendationRuleSet;
import org.skypro.projects.personaloffers.service.RuleSetsEvaluator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaticRulesEvaluator implements RuleSetsEvaluator {
    private final List<RecommendationRuleSet> staticRulesSets;
    private final List<Product> productsToOffer = new ArrayList<>(3);

    public StaticRulesEvaluator(List<RecommendationRuleSet> staticRulesSets) {
        this.staticRulesSets = staticRulesSets;
    }

    public List<Product> getProductsForClient(UUID clientId) {
        for (RecommendationRuleSet ruleSet : this.staticRulesSets) {
            Optional<Product> offer = this.evaluate(ruleSet, clientId);
            offer.ifPresent(this.productsToOffer::add);
        }

        return this.productsToOffer;
    }

    protected Optional<Product> evaluate(RecommendationRuleSet ruleSet, UUID userId) {
        return ruleSet.applyRules(userId);
    }
}
