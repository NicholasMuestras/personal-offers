-- liquibase formatted sql

-- changeset nobraztsov:1
CREATE TABLE rule_stats (
    id UUID PRIMARY KEY,
    rule_id UUID NOT NULL,
    count BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_rule_stats_rule_id ON rule_stats (rule_id);
