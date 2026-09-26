package com.example.voucher.dto;

import com.example.voucher.entity.VoucherScope;
import java.time.LocalDate;

public class MerchantVoucherRequest {
    private String code;
    private String description;
    private Double discount;
    private LocalDate expiryDate;
    private Integer maxUsage;
    
    // Merchant specific fields
    private VoucherScope scope;
    private Double minPurchaseAmount;
    private Double maxDiscount;
    private String termsAndConditions;
    private String promotionName;

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Integer getMaxUsage() { return maxUsage; }
    public void setMaxUsage(Integer maxUsage) { this.maxUsage = maxUsage; }

    public VoucherScope getScope() { return scope; }
    public void setScope(VoucherScope scope) { this.scope = scope; }

    public Double getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }

    public Double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }

    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }

    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }
}
