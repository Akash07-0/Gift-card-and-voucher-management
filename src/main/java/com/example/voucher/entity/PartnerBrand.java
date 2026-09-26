package com.example.voucher.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "partner_brands")
public class PartnerBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String brandName;

    private String logo;
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationMode redemptionMode = IntegrationMode.DEMO;

    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public IntegrationMode getRedemptionMode() { return redemptionMode; }
    public void setRedemptionMode(IntegrationMode redemptionMode) { this.redemptionMode = redemptionMode; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
