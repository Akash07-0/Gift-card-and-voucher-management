package com.example.voucher.service;

import com.example.voucher.entity.GiftCard;
import com.example.voucher.entity.User;
import com.example.voucher.repository.GiftCardRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.service.integration.DemoIntegrationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CustomerRewardService {

    private final GiftCardRepository giftCardRepository;
    private final UserRepository userRepository;
    private final DemoIntegrationProvider demoIntegrationProvider;

    public CustomerRewardService(GiftCardRepository giftCardRepository, UserRepository userRepository, DemoIntegrationProvider demoIntegrationProvider) {
        this.giftCardRepository = giftCardRepository;
        this.userRepository = userRepository;
        this.demoIntegrationProvider = demoIntegrationProvider;
    }

    public List<GiftCard> getMyRewards(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail).orElseThrow();
        return giftCardRepository.findAll().stream()
                .filter(gc -> gc.getCreatedBy() != null && gc.getCreatedBy().getId().equals(customer.getId()))
                .filter(gc -> gc.getPartnerBrand() != null)
                .toList();
    }

    public GiftCard getReward(String customerEmail, Long rewardId) {
        User customer = userRepository.findByEmail(customerEmail).orElseThrow();
        GiftCard gc = giftCardRepository.findById(rewardId).orElseThrow(() -> new IllegalArgumentException("Reward not found"));
        if (gc.getCreatedBy() == null || !gc.getCreatedBy().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Unauthorized reward access");
        }
        return gc;
    }

    @Transactional
    public Map<String, String> redeemReward(String customerEmail, Long rewardId) {
        GiftCard reward = getReward(customerEmail, rewardId);

        if (!reward.isActive() || reward.getBalance() <= 0) {
            throw new IllegalStateException("Reward already redeemed or inactive");
        }
        
        if (reward.getExpiryDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException("Reward expired");
        }

        // Use DemoIntegrationProvider
        Map<String, String> result = demoIntegrationProvider.redeemReward(reward, customerEmail);
        
        // Mark redeemed
        reward.setActive(false);
        reward.setBalance(0.0);
        giftCardRepository.save(reward);
        
        return result;
    }
}
