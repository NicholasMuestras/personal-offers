package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OffersService {

    protected List<RuleSetsEvaluator> evaluators;
    private final UserService userService;

    public OffersService(List<RuleSetsEvaluator> evaluators, UserService userService) {
        this.evaluators = evaluators;
        this.userService = userService;
    }

    public List<Product> getRecommendations(UUID userId) {
        List<Product> recommendedOffers = new ArrayList<>(10);

        for (RuleSetsEvaluator evaluator : this.evaluators) {
            recommendedOffers.addAll(evaluator.getProductsForClient(userId));
        }

        return recommendedOffers;
    }
}
