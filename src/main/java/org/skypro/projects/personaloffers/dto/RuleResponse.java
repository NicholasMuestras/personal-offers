package org.skypro.projects.personaloffers.dto;

import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.entity.Term;

import java.util.List;
import java.util.UUID;

public class RuleResponse {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;
    private List<Term> rule;

    public RuleResponse() {
    }

    public RuleResponse(DynamicRule dynamicRule) {
        this.id = dynamicRule.getId();

        if (dynamicRule.getProduct() != null) {
            this.productName = dynamicRule.getProduct().getName();
            this.productId = dynamicRule.getProduct().getId();
        }

        this.rule = dynamicRule.getTerms();
        this.productText = dynamicRule.getProduct() != null
                ? dynamicRule.getProduct().getDescription()
                : null
        ;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<Term> getRule() {
        return rule;
    }

    public void setRule(List<Term> rule) {
        this.rule = rule;
    }
}
