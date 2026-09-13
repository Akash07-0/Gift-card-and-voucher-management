package com.example.voucher.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record VoucherRequest(
    @NotBlank String code,
    @NotBlank String description,
    @Positive Double discount,
    @Future LocalDate expiryDate,
    @Positive Integer maxUsage
) {}
