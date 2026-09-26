package com.example.voucher.dto;

public record AdminStatsResponse(
    long totalVouchers,
    long activeVouchers,
    long totalGiftCards,
    long activeGiftCards,
    long totalRedemptions,
    long totalCustomers
) {}
