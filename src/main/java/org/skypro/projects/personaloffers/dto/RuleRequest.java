package org.skypro.projects.personaloffers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.skypro.projects.personaloffers.entity.Term;

import java.util.List;
import java.util.UUID;

public class RuleRequest {
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_text")
    private String productText;

    private List<Term> rule;

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
