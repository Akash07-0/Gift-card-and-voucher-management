package com.example.voucher.dto;

import jakarta.validation.constraints.NotBlank;

public record RedeemRequest(
    @NotBlank
    String code
) {
}

