package com.example.voucher.dto;

import com.example.voucher.entity.Redemption;
import java.time.LocalDateTime;

public record RedemptionResponse(
    Long id, String voucherCode, Double discount, LocalDateTime redeemedAt, String status
) {
    public static RedemptionResponse from(Redemption r) {
        return new RedemptionResponse(
            r.getId(), r.getVoucher().getCode(), r.getVoucher().getDiscount(),
            r.getRedeemedAt(), r.getStatus()
        );
    }
}
