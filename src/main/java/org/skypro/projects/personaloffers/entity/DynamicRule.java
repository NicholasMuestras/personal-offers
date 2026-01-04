package org.skypro.projects.personaloffers.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.skypro.projects.personaloffers.service.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "rules")
public class DynamicRule implements RecommendationRuleSet {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Term> terms = List.of();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    public DynamicRule() {
    }

    public DynamicRule(List<Term> terms, Product product) {
        this.terms = terms;
        this.product = product;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public List<Term> getTerms() {
        return terms;
    }

    @Override
    public Optional<org.skypro.projects.personaloffers.model.Product> applyRules(UUID userId) {

//        for (Term term : terms) {
//            if (term.isNegate()) {
//                if (term.getQuery().equals("hasPurchased")) {
//                    if (term.getArguments().contains(userId.toString())) {
//                        return Optional.of(new org.skypro.projects.personaloffers.model.Product(product.getId(), product.getType(), product.getName(), product.getDescription()));
//                    }
//                }
//        }

        return Optional.empty();
    }
}
