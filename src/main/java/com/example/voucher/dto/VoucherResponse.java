package com.example.voucher.dto;

import com.example.voucher.entity.Voucher;
import java.time.LocalDate;

public record VoucherResponse(
    Long id, String code, String description, Double discount,
    LocalDate expiryDate, Integer maxUsage, Integer currentUsage, boolean active
) {
    public static VoucherResponse from(Voucher v) {
        return new VoucherResponse(
            v.getId(), v.getCode(), v.getDescription(), v.getDiscount(),
            v.getExpiryDate(), v.getMaxUsage(), v.getCurrentUsage(), v.isActive()
        );
    }
}
