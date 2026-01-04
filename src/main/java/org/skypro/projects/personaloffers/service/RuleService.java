package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.entity.Product;
import org.skypro.projects.personaloffers.entity.Term;
import org.skypro.projects.personaloffers.repository.DynamicRuleRepository;
import org.skypro.projects.personaloffers.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class RuleService {

    @Autowired
    private DynamicRuleRepository ruleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public DynamicRule save(DynamicRule rule) {
        return ruleRepository.save(rule);
    }

    public void delete(UUID id) {
        ruleRepository.deleteById(id);
    }

    public List<DynamicRule> getAll() {
        return ruleRepository.findAll();
    }

    public DynamicRule getRuleById(UUID id) {
        return ruleRepository.findById(id).orElse(null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DynamicRule createRule(UUID productId, String productName, String productText, List<Term> terms) {
        Logger logger = Logger.getLogger(RuleService.class.getName());
        logger.info("Creating rule for product ID: " + productId);

        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            logger.info("Product with ID " + productId + " not found, creating new product");
            product = new Product();
            product.setId(productId);
            product.setName(productName);
            product.setDescription(productText);
            product = productRepository.save(product);
            logger.info("Created new product: " + product);
        } else {
            logger.info("Found existing product: " + product + ", using without modification");
        }

        DynamicRule rule = new DynamicRule();
        rule.setTerms(terms);
        rule.setProduct(product);
        return rule;
    }
}
