package com.example.voucher.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "gift_cards")
public class GiftCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    // ACTIVE / INACTIVE STATUS
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "partner_brand_id")
    private PartnerBrand partnerBrand;

    public GiftCard() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public PartnerBrand getPartnerBrand() {
        return partnerBrand;
    }

    public void setPartnerBrand(PartnerBrand partnerBrand) {
        this.partnerBrand = partnerBrand;
    }
}