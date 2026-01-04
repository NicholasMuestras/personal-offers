package org.skypro.projects.personaloffers.controller;

import org.skypro.projects.personaloffers.dto.RuleRequest;
import org.skypro.projects.personaloffers.dto.RuleResponse;
import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping
    public ResponseEntity<RuleResponse> addRule(@RequestBody RuleRequest request) {
        DynamicRule rule = ruleService.createRule(
                request.getProductId(),
                request.getProductName(),
                request.getProductText(),
                request.getRule()
        );

        DynamicRule savedRule = ruleService.save(rule);
        return ResponseEntity.ok(new RuleResponse(savedRule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        ruleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RuleResponse>> getRules() {
        List<DynamicRule> rules = ruleService.getAll();
        List<RuleResponse> response = rules.stream()
                .map(RuleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
