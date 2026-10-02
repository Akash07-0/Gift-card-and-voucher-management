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
    private long totalRewardsIssued;
    private long totalRewardsRedeemed;
    private long activePromotions;
    private long activeCustomers;
    private double totalPurchaseValue;

    private java.util.List<TimeSeriesDataPoint> purchasesOverTime;
    private java.util.List<TimeSeriesDataPoint> redemptionsOverTime;
    private java.util.List<TimeSeriesDataPoint> rewardsIssuedOverTime;
    private java.util.List<TimeSeriesDataPoint> rewardsRedeemedOverTime;
    private java.util.List<TimeSeriesDataPoint> voucherUsageOverTime;
    
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

    public long getTotalRewardsIssued() { return totalRewardsIssued; }
    public void setTotalRewardsIssued(long totalRewardsIssued) { this.totalRewardsIssued = totalRewardsIssued; }
    public long getTotalRewardsRedeemed() { return totalRewardsRedeemed; }
    public void setTotalRewardsRedeemed(long totalRewardsRedeemed) { this.totalRewardsRedeemed = totalRewardsRedeemed; }
    public long getActivePromotions() { return activePromotions; }
    public void setActivePromotions(long activePromotions) { this.activePromotions = activePromotions; }
    public long getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(long activeCustomers) { this.activeCustomers = activeCustomers; }
    public double getTotalPurchaseValue() { return totalPurchaseValue; }
    public void setTotalPurchaseValue(double totalPurchaseValue) { this.totalPurchaseValue = totalPurchaseValue; }

    public java.util.List<TimeSeriesDataPoint> getPurchasesOverTime() { return purchasesOverTime; }
    public void setPurchasesOverTime(java.util.List<TimeSeriesDataPoint> purchasesOverTime) { this.purchasesOverTime = purchasesOverTime; }
    public java.util.List<TimeSeriesDataPoint> getRedemptionsOverTime() { return redemptionsOverTime; }
    public void setRedemptionsOverTime(java.util.List<TimeSeriesDataPoint> redemptionsOverTime) { this.redemptionsOverTime = redemptionsOverTime; }
    public java.util.List<TimeSeriesDataPoint> getRewardsIssuedOverTime() { return rewardsIssuedOverTime; }
    public void setRewardsIssuedOverTime(java.util.List<TimeSeriesDataPoint> rewardsIssuedOverTime) { this.rewardsIssuedOverTime = rewardsIssuedOverTime; }
    public java.util.List<TimeSeriesDataPoint> getRewardsRedeemedOverTime() { return rewardsRedeemedOverTime; }
    public void setRewardsRedeemedOverTime(java.util.List<TimeSeriesDataPoint> rewardsRedeemedOverTime) { this.rewardsRedeemedOverTime = rewardsRedeemedOverTime; }
    public java.util.List<TimeSeriesDataPoint> getVoucherUsageOverTime() { return voucherUsageOverTime; }
    public void setVoucherUsageOverTime(java.util.List<TimeSeriesDataPoint> voucherUsageOverTime) { this.voucherUsageOverTime = voucherUsageOverTime; }
}
