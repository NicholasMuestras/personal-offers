package org.skypro.projects.personaloffers.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.skypro.projects.personaloffers.entity.Product;
import org.skypro.projects.personaloffers.converter.TermListConverter;

@Entity
@Table(name = "rules")
public class DynamicRule {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = TermListConverter.class)
    private List<Term> terms = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public DynamicRule() {
    }

    public DynamicRule(List<Term> terms, Product product) {
        this.terms = terms;
        this.product = product;
    }
}
