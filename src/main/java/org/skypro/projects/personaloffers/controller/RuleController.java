package org.skypro.projects.personaloffers.controller;

import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping
    public ResponseEntity<DynamicRule> addRule(@RequestBody DynamicRule rule) {
        DynamicRule savedRule = ruleService.save(rule);
        return ResponseEntity.ok(savedRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        ruleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DynamicRule>> getRules() {
        List<DynamicRule> rules = ruleService.getAll();
        return ResponseEntity.ok(rules);
    }
}
