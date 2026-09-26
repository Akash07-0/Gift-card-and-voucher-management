package com.example.voucher.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record VoucherRequest(
    @NotBlank(message = "Voucher code is required")
    String code,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Discount is required")
    @Positive(message = "Discount must be greater than 0")
    Double discount,

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    LocalDate expiryDate,

    @NotNull(message = "Maximum usage is required")
    @Positive(message = "Maximum usage must be greater than 0")
    Integer maxUsage,

    com.example.voucher.entity.DiscountType discountType,

    String currency
) {}

