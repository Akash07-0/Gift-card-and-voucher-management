package com.example.voucher.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reward_rules")
public class RewardRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(nullable = false)
    private Double minimumPurchaseAmount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partner_brand_id")
    private PartnerBrand rewardBrand;

    private String rewardType = "GIFT_CARD";
    
    @Column(nullable = false)
    private Double rewardAmount;
    
    @Column(nullable = false)
    private String currency = "INR";
    
    private boolean active = true;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public Double getMinimumPurchaseAmount() { return minimumPurchaseAmount; }
    public void setMinimumPurchaseAmount(Double minimumPurchaseAmount) { this.minimumPurchaseAmount = minimumPurchaseAmount; }
    public PartnerBrand getRewardBrand() { return rewardBrand; }
    public void setRewardBrand(PartnerBrand rewardBrand) { this.rewardBrand = rewardBrand; }
    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }
    public Double getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(Double rewardAmount) { this.rewardAmount = rewardAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
