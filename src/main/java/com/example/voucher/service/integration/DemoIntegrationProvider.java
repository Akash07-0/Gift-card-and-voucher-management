package com.example.voucher.service.integration;

import com.example.voucher.entity.GiftCard;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DemoIntegrationProvider implements BrandIntegrationProvider {

    @Override
    public boolean validateReward(GiftCard reward) {
        return reward.isActive() && reward.getBalance() > 0 && 
               reward.getExpiryDate().isAfter(java.time.LocalDate.now().minusDays(1));
    }

    @Override
    public Map<String, String> redeemReward(GiftCard reward, String customerEmail) {
        if (!validateReward(reward)) {
            throw new IllegalStateException("Reward is invalid or expired.");
        }
        
        String reference = "DEMO-" + reward.getPartnerBrand().getBrandName().toUpperCase() + "-" + 
                           UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("reference", reference);
        response.put("message", "Demo Partner Redemption Successful");
        
        return response;
    }

    @Override
    public String getRedemptionInstructions() {
        return "This is a demo partner redemption. No real external API is called.";
    }
}
