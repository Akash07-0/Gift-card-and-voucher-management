package com.example.voucher.dto;

import java.time.LocalDateTime;

public class PurchaseResponse {
    private Long id;
    private String orderId;
    private Long customerId;
    private Long shopId;
    private Double amount;
    private String currency;
    private LocalDateTime purchaseDate;
    private Long issuedVoucherId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public Long getIssuedVoucherId() { return issuedVoucherId; }
    public void setIssuedVoucherId(Long issuedVoucherId) { this.issuedVoucherId = issuedVoucherId; }
}
