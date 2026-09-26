package com.example.voucher.dto;

public class PurchaseRequest {
    private Long shopId;
    private Double amount;
    private String currency;

    // Getters and Setters
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
