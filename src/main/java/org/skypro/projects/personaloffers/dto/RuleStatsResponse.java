package org.skypro.projects.personaloffers.dto;

import java.util.UUID;

public class RuleStatsResponse {
    private UUID ruleId;
    private Long count;

    public RuleStatsResponse() {
    }

    public RuleStatsResponse(UUID ruleId, Long count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}