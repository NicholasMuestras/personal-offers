package org.skypro.projects.personaloffers.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "rule_stats")
public class RuleStats {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "rule_id")
    private UUID ruleId;

    @Column(name = "count")
    private long count;

    public RuleStats() {
    }

    public RuleStats(UUID ruleId, long count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}