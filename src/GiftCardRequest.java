package com.example.voucher.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record GiftCardRequest(

        @NotBlank
        String code,

        @Positive
        Double amount,

        @Future
        LocalDate expiryDate

) {
}