package com.example.voucher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record GiftCardRedeemRequest(

        @NotBlank
        String code,

        @Positive
        Double amount

) {
}