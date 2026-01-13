package org.skypro.projects.personaloffers.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rules")
public class DynamicRule {
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
}
