package com.example.voucher.dto;

import java.util.List;
import java.util.Map;

public class AdminStatsResponse {
    private long totalUsers;
    private long totalCustomers;
    private long totalMerchants;
    private long totalVouchers;
    private long activeVouchers;
    private long inactiveVouchers;
    private long totalGiftCards;
    private long activeGiftCards;
    private long inactiveGiftCards;
    private long totalRedemptions;
    private long totalPurchases;
    private long totalRewardsIssued;
    private long totalRewardsRedeemed;
    private double totalDiscountValue;
    private long activePartnerBrands;

    private List<TimeSeriesDataPoint> voucherActivityOverTime;
    private List<TimeSeriesDataPoint> redemptionActivityOverTime;
    private List<TimeSeriesDataPoint> purchaseActivityOverTime;
    private List<TimeSeriesDataPoint> rewardIssuanceOverTime;
    private List<TimeSeriesDataPoint> rewardRedemptionOverTime;
    private List<TimeSeriesDataPoint> merchantActivityOverTime;

    private Map<String, Long> voucherStatusDistribution;
    private Map<String, Long> giftCardStatusDistribution;

    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getTotalMerchants() { return totalMerchants; }
    public void setTotalMerchants(long totalMerchants) { this.totalMerchants = totalMerchants; }
    public long getTotalVouchers() { return totalVouchers; }
    public void setTotalVouchers(long totalVouchers) { this.totalVouchers = totalVouchers; }
    public long getActiveVouchers() { return activeVouchers; }
    public void setActiveVouchers(long activeVouchers) { this.activeVouchers = activeVouchers; }
    public long getInactiveVouchers() { return inactiveVouchers; }
    public void setInactiveVouchers(long inactiveVouchers) { this.inactiveVouchers = inactiveVouchers; }
    public long getTotalGiftCards() { return totalGiftCards; }
    public void setTotalGiftCards(long totalGiftCards) { this.totalGiftCards = totalGiftCards; }
    public long getActiveGiftCards() { return activeGiftCards; }
    public void setActiveGiftCards(long activeGiftCards) { this.activeGiftCards = activeGiftCards; }
    public long getInactiveGiftCards() { return inactiveGiftCards; }
    public void setInactiveGiftCards(long inactiveGiftCards) { this.inactiveGiftCards = inactiveGiftCards; }
    public long getTotalRedemptions() { return totalRedemptions; }
    public void setTotalRedemptions(long totalRedemptions) { this.totalRedemptions = totalRedemptions; }
    public long getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(long totalPurchases) { this.totalPurchases = totalPurchases; }
    public long getTotalRewardsIssued() { return totalRewardsIssued; }
    public void setTotalRewardsIssued(long totalRewardsIssued) { this.totalRewardsIssued = totalRewardsIssued; }
    public long getTotalRewardsRedeemed() { return totalRewardsRedeemed; }
    public void setTotalRewardsRedeemed(long totalRewardsRedeemed) { this.totalRewardsRedeemed = totalRewardsRedeemed; }
    public double getTotalDiscountValue() { return totalDiscountValue; }
    public void setTotalDiscountValue(double totalDiscountValue) { this.totalDiscountValue = totalDiscountValue; }
    public long getActivePartnerBrands() { return activePartnerBrands; }
    public void setActivePartnerBrands(long activePartnerBrands) { this.activePartnerBrands = activePartnerBrands; }

    public List<TimeSeriesDataPoint> getVoucherActivityOverTime() { return voucherActivityOverTime; }
    public void setVoucherActivityOverTime(List<TimeSeriesDataPoint> voucherActivityOverTime) { this.voucherActivityOverTime = voucherActivityOverTime; }
    public List<TimeSeriesDataPoint> getRedemptionActivityOverTime() { return redemptionActivityOverTime; }
    public void setRedemptionActivityOverTime(List<TimeSeriesDataPoint> redemptionActivityOverTime) { this.redemptionActivityOverTime = redemptionActivityOverTime; }
    public List<TimeSeriesDataPoint> getPurchaseActivityOverTime() { return purchaseActivityOverTime; }
    public void setPurchaseActivityOverTime(List<TimeSeriesDataPoint> purchaseActivityOverTime) { this.purchaseActivityOverTime = purchaseActivityOverTime; }
    public List<TimeSeriesDataPoint> getRewardIssuanceOverTime() { return rewardIssuanceOverTime; }
    public void setRewardIssuanceOverTime(List<TimeSeriesDataPoint> rewardIssuanceOverTime) { this.rewardIssuanceOverTime = rewardIssuanceOverTime; }
    public List<TimeSeriesDataPoint> getRewardRedemptionOverTime() { return rewardRedemptionOverTime; }
    public void setRewardRedemptionOverTime(List<TimeSeriesDataPoint> rewardRedemptionOverTime) { this.rewardRedemptionOverTime = rewardRedemptionOverTime; }
    public List<TimeSeriesDataPoint> getMerchantActivityOverTime() { return merchantActivityOverTime; }
    public void setMerchantActivityOverTime(List<TimeSeriesDataPoint> merchantActivityOverTime) { this.merchantActivityOverTime = merchantActivityOverTime; }

    public Map<String, Long> getVoucherStatusDistribution() { return voucherStatusDistribution; }
    public void setVoucherStatusDistribution(Map<String, Long> voucherStatusDistribution) { this.voucherStatusDistribution = voucherStatusDistribution; }
    public Map<String, Long> getGiftCardStatusDistribution() { return giftCardStatusDistribution; }
    public void setGiftCardStatusDistribution(Map<String, Long> giftCardStatusDistribution) { this.giftCardStatusDistribution = giftCardStatusDistribution; }
}
