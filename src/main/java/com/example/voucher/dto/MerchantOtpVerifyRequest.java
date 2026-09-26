package com.example.voucher.dto;

public class MerchantOtpVerifyRequest {
    private String voucherCode;
    private Long customerId;
    private Double purchaseAmount;
    private String otp;

    // Getters and Setters
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getPurchaseAmount() { return purchaseAmount; }
    public void setPurchaseAmount(Double purchaseAmount) { this.purchaseAmount = purchaseAmount; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
