package com.example.voucher.controller;

import com.example.voucher.entity.GiftCard;
import com.example.voucher.service.CustomerRewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/rewards")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerRewardController {

    private final CustomerRewardService service;

    public CustomerRewardController(CustomerRewardService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<GiftCard>> getMyRewards(Authentication authentication) {
        return ResponseEntity.ok(service.getMyRewards(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiftCard> getReward(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(service.getReward(authentication.getName(), id));
    }

    @PostMapping("/{id}/redeem")
    public ResponseEntity<Map<String, String>> redeemReward(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(service.redeemReward(authentication.getName(), id));
    }
}
