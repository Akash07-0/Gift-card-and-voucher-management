package com.example.voucher.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record GiftCardRequest(

        @NotBlank(message = "Gift card code is required")
        String code,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        Double amount,

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        String currency

) {
}