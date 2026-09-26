package com.example.voucher.service;

import com.example.voucher.entity.RewardRule;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.User;
import com.example.voucher.repository.RewardRuleRepository;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardRuleService {

    private final RewardRuleRepository rewardRuleRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public RewardRuleService(RewardRuleRepository rewardRuleRepository, ShopRepository shopRepository, UserRepository userRepository) {
        this.rewardRuleRepository = rewardRuleRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    private Shop getMerchantShop(String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));
        return shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
    }

    public List<RewardRule> getRules(String merchantEmail) {
        Shop shop = getMerchantShop(merchantEmail);
        // Return all rules for shop, not just active
        return rewardRuleRepository.findAll().stream()
                .filter(r -> r.getShop().getId().equals(shop.getId()))
                .toList();
    }

    public RewardRule createRule(String merchantEmail, RewardRule rule) {
        Shop shop = getMerchantShop(merchantEmail);
        rule.setShop(shop);
        return rewardRuleRepository.save(rule);
    }

    public RewardRule updateRule(String merchantEmail, Long ruleId, RewardRule ruleUpdate) {
        Shop shop = getMerchantShop(merchantEmail);
        RewardRule existing = rewardRuleRepository.findById(ruleId).orElseThrow();
        if (!existing.getShop().getId().equals(shop.getId())) {
            throw new IllegalArgumentException("Unauthorized rule access");
        }
        existing.setMinimumPurchaseAmount(ruleUpdate.getMinimumPurchaseAmount());
        existing.setRewardBrand(ruleUpdate.getRewardBrand());
        existing.setRewardType(ruleUpdate.getRewardType());
        existing.setRewardAmount(ruleUpdate.getRewardAmount());
        existing.setCurrency(ruleUpdate.getCurrency());
        return rewardRuleRepository.save(existing);
    }

    public void activateRule(String merchantEmail, Long ruleId) {
        Shop shop = getMerchantShop(merchantEmail);
        RewardRule existing = rewardRuleRepository.findById(ruleId).orElseThrow();
        if (!existing.getShop().getId().equals(shop.getId())) {
            throw new IllegalArgumentException("Unauthorized rule access");
        }
        existing.setActive(true);
        rewardRuleRepository.save(existing);
    }

    public void deactivateRule(String merchantEmail, Long ruleId) {
        Shop shop = getMerchantShop(merchantEmail);
        RewardRule existing = rewardRuleRepository.findById(ruleId).orElseThrow();
        if (!existing.getShop().getId().equals(shop.getId())) {
            throw new IllegalArgumentException("Unauthorized rule access");
        }
        existing.setActive(false);
        rewardRuleRepository.save(existing);
    }
}
