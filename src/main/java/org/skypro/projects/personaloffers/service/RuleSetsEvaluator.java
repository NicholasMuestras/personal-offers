package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Product;

import java.util.List;
import java.util.UUID;

public interface RuleSetsEvaluator {
    List<Product> getProductsForClient(UUID clientId);
}
