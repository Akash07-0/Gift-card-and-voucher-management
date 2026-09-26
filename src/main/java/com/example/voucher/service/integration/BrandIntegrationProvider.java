package com.example.voucher.service.integration;

import com.example.voucher.entity.GiftCard;
import java.util.Map;

public interface BrandIntegrationProvider {
    boolean validateReward(GiftCard reward);
    Map<String, String> redeemReward(GiftCard reward, String customerEmail);
    String getRedemptionInstructions();
}
