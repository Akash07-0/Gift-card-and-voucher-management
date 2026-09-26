package com.example.voucher.controller;

import com.example.voucher.entity.RewardRule;
import com.example.voucher.service.RewardRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/reward-rules")
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantRewardRuleController {

    private final RewardRuleService service;

    public MerchantRewardRuleController(RewardRuleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RewardRule>> getRules(Authentication authentication) {
        return ResponseEntity.ok(service.getRules(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<RewardRule> createRule(Authentication authentication, @RequestBody RewardRule rule) {
        return ResponseEntity.ok(service.createRule(authentication.getName(), rule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RewardRule> updateRule(Authentication authentication, @PathVariable Long id, @RequestBody RewardRule rule) {
        return ResponseEntity.ok(service.updateRule(authentication.getName(), id, rule));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateRule(Authentication authentication, @PathVariable Long id) {
        service.activateRule(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateRule(Authentication authentication, @PathVariable Long id) {
        service.deactivateRule(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }
}
