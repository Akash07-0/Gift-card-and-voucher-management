package com.example.voucher.dto;

import com.example.voucher.entity.GiftCardRedemption;

import java.time.LocalDateTime;

public record GiftCardRedemptionResponse(
        Long id,
        String giftCardCode,
        String userEmail,
        Double amount,
        Double remainingBalance,
        LocalDateTime redeemedAt
) {
    public static GiftCardRedemptionResponse from(GiftCardRedemption redemption) {
        return new GiftCardRedemptionResponse(
                redemption.getId(),
                redemption.getGiftCard().getCode(),
                redemption.getUser().getEmail(),
                redemption.getAmount(),
                redemption.getRemainingBalance(),
                redemption.getRedeemedAt());
    }
}
