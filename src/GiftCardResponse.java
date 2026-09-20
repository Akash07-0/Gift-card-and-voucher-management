package com.example.voucher.dto;

import com.example.voucher.entity.GiftCard;

import java.time.LocalDate;

public record GiftCardResponse(

        Long id,
        String code,
        Double amount,
        Double balance,
        LocalDate expiryDate,
        boolean active

) {

    public static GiftCardResponse from(GiftCard giftCard) {

        return new GiftCardResponse(
                giftCard.getId(),
                giftCard.getCode(),
                giftCard.getAmount(),
                giftCard.getBalance(),
                giftCard.getExpiryDate(),
                giftCard.isActive()
        );
    }
}