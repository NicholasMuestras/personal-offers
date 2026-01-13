package org.skypro.projects.personaloffers.service.evaluator;

import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.entity.Term;
import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.repository.DynamicRuleRepository;
import org.skypro.projects.personaloffers.repository.ProductExternalRepository;
import org.skypro.projects.personaloffers.repository.RuleStatsRepository;
import org.skypro.projects.personaloffers.service.RuleSetsEvaluator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DynamicRulesEvaluator implements RuleSetsEvaluator {
    private final List<Product> productsToOffer = new ArrayList<>(10);
    private final DynamicRuleRepository dynamicRuleRepository;
    private final ProductExternalRepository productExternalRepository;
    private final RuleStatsRepository ruleStatsRepository;

    public DynamicRulesEvaluator(
            DynamicRuleRepository dynamicRuleRepository,
            ProductExternalRepository productExternalRepository,
            RuleStatsRepository ruleStatsRepository
    ) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.productExternalRepository = productExternalRepository;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    public List<Product> getProductsForClient(UUID clientId) {
        List<DynamicRule> dynamicRulesSets = this.dynamicRuleRepository.findAll();
        List<UUID> rulesIds = new ArrayList<>(dynamicRulesSets.size());

        for (DynamicRule ruleSet : dynamicRulesSets) {
            Optional<Product> offer = this.evaluate(ruleSet, clientId);

            if (offer.isPresent()) {
                this.productsToOffer.add(offer.get());
                rulesIds.add(ruleSet.getId());
            }
        }

        if (!rulesIds.isEmpty()) {
            this.ruleStatsRepository.incrementCountByRuleIds(rulesIds);
        }

        return this.productsToOffer;
    }

    protected Optional<Product> evaluate(DynamicRule ruleSet, UUID userId) {
        boolean allRulesValid = true;
        boolean isRuleValid;
        
        for (Term term: ruleSet.getTerms()) {
            isRuleValid = this.evaluateRule(term, userId);
            
            if (isRuleValid && !term.isNegate() || !isRuleValid && term.isNegate()) {

            } else {
                allRulesValid = false;
                break;
            }
        }
        
        if (allRulesValid) {
            return Optional.of(this.mapProduct(ruleSet));
        }

        return Optional.empty();
    }

    private boolean evaluateRule(Term term, UUID userId) {
        String queryName = term.getQuery();
        List<String> arguments = term.getArguments();

        return productExternalRepository.evaluateQuery(queryName, userId, arguments);
    }
    
    private Product mapProduct(DynamicRule dynamicRule) {
        return new Product(
                dynamicRule.getProduct().getId(),
                dynamicRule.getProduct().getType(),
                dynamicRule.getProduct().getName(),
                dynamicRule.getProduct().getDescription()
        );
    }
}
