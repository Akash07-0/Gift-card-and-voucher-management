package com.example.voucher.dto;

import com.example.voucher.entity.VoucherScope;
import java.time.LocalDate;

public class MerchantRedemptionVerifyResponse {
    private Long voucherId;
    private String voucherCode;
    private String promotionName;
    private Double discount;
    private Double maxDiscount;
    private Long customerId;
    private String customerName;
    private Long shopId;
    private String shopName;
    private Double purchaseAmount;
    private Double minimumPurchaseAmount;
    private LocalDate expiryDate;
    private Integer currentUsage;
    private Integer maximumUsage;
    private VoucherScope scope;
    private String status;
    private boolean otpRequired;

    // Getters and Setters
    public Long getVoucherId() { return voucherId; }
    public void setVoucherId(Long voucherId) { this.voucherId = voucherId; }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public Double getPurchaseAmount() { return purchaseAmount; }
    public void setPurchaseAmount(Double purchaseAmount) { this.purchaseAmount = purchaseAmount; }

    public Double getMinimumPurchaseAmount() { return minimumPurchaseAmount; }
    public void setMinimumPurchaseAmount(Double minimumPurchaseAmount) { this.minimumPurchaseAmount = minimumPurchaseAmount; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Integer getCurrentUsage() { return currentUsage; }
    public void setCurrentUsage(Integer currentUsage) { this.currentUsage = currentUsage; }

    public Integer getMaximumUsage() { return maximumUsage; }
    public void setMaximumUsage(Integer maximumUsage) { this.maximumUsage = maximumUsage; }

    public VoucherScope getScope() { return scope; }
    public void setScope(VoucherScope scope) { this.scope = scope; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isOtpRequired() { return otpRequired; }
    public void setOtpRequired(boolean otpRequired) { this.otpRequired = otpRequired; }
}
