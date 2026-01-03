package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.entity.DynamicRule;
import org.skypro.projects.personaloffers.repository.DynamicRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleService {

    @Autowired
    private DynamicRuleRepository ruleRepository;

    public DynamicRule save(DynamicRule rule) {
        return ruleRepository.save(rule);
    }

    public void delete(UUID id) {
        ruleRepository.deleteById(id);
    }

    public List<DynamicRule> getAll() {
        return ruleRepository.findAll();
    }

    public DynamicRule getRuleById(UUID id) {
        return ruleRepository.findById(id).orElse(null);
    }
}
