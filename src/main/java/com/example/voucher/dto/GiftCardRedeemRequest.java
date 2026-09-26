package com.example.voucher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GiftCardRedeemRequest(

        @NotBlank(message = "Gift card code is required")
        String code,

        @NotNull(message = "Redeem amount is required")
        @Positive(message = "Redeem amount must be greater than 0")
        Double amount

) {
}