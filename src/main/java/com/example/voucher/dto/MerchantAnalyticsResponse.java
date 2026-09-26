package com.example.voucher.dto;

public class MerchantAnalyticsResponse {
    private long totalVouchers;
    private long activeVouchers;
    private long redeemedVouchers;
    private long expiredVouchers;
    private long totalPurchases;
    private long successfulRedemptions;
    private long failedRedemptions;
    private double totalDiscountGiven;

    // Getters and Setters
    public long getTotalVouchers() { return totalVouchers; }
    public void setTotalVouchers(long totalVouchers) { this.totalVouchers = totalVouchers; }

    public long getActiveVouchers() { return activeVouchers; }
    public void setActiveVouchers(long activeVouchers) { this.activeVouchers = activeVouchers; }

    public long getRedeemedVouchers() { return redeemedVouchers; }
    public void setRedeemedVouchers(long redeemedVouchers) { this.redeemedVouchers = redeemedVouchers; }

    public long getExpiredVouchers() { return expiredVouchers; }
    public void setExpiredVouchers(long expiredVouchers) { this.expiredVouchers = expiredVouchers; }

    public long getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(long totalPurchases) { this.totalPurchases = totalPurchases; }

    public long getSuccessfulRedemptions() { return successfulRedemptions; }
    public void setSuccessfulRedemptions(long successfulRedemptions) { this.successfulRedemptions = successfulRedemptions; }

    public long getFailedRedemptions() { return failedRedemptions; }
    public void setFailedRedemptions(long failedRedemptions) { this.failedRedemptions = failedRedemptions; }

    public double getTotalDiscountGiven() { return totalDiscountGiven; }
    public void setTotalDiscountGiven(double totalDiscountGiven) { this.totalDiscountGiven = totalDiscountGiven; }
}
