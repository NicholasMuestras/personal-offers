package org.skypro.projects.personaloffers.controller;

import org.skypro.projects.personaloffers.dto.RuleRequest;
import org.skypro.projects.personaloffers.dto.RuleResponse;
import org.skypro.projects.personaloffers.dto.RuleStatsResponse;
import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.service.DynamicRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private DynamicRuleService dynamicRuleService;

    @GetMapping("/stats")
    public ResponseEntity<List<RuleStatsResponse>> getRuleStats() {
        Iterable<Object[]> stats = dynamicRuleService.getRuleStats();
        List<RuleStatsResponse> response = StreamSupport.stream(stats.spliterator(), false)
                .map(row -> new RuleStatsResponse((UUID) row[0], (Long) row[1]))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RuleResponse> addRule(@RequestBody RuleRequest request) {
        DynamicRule rule = dynamicRuleService.createRule(
                request.getProductId(),
                request.getProductName(),
                request.getProductText(),
                request.getRule()
        );

        DynamicRule savedRule = dynamicRuleService.save(rule);
        return ResponseEntity.ok(new RuleResponse(savedRule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        dynamicRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RuleResponse>> getRules() {
        List<DynamicRule> rules = dynamicRuleService.getAll();
        List<RuleResponse> response = rules.stream()
                .map(RuleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
