package org.skypro.projects.personaloffers.repository;

import org.skypro.projects.personaloffers.entity.RuleStats;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleStatsRepository extends CrudRepository<RuleStats, UUID> {

    @Query(value = "SELECT rule_id, COUNT(*) as count FROM rule_stats GROUP BY rule_id", nativeQuery = true)
    List<Object[]> getRuleStats();

    void deleteByRuleId(UUID ruleId);

    @Modifying
    @Query(value = "UPDATE rule_stats SET count = count + 1 WHERE rule_id IN :ruleIds", nativeQuery = true)
    void incrementCountByRuleIds(@Param("ruleIds") List<UUID> ruleIds);
}
