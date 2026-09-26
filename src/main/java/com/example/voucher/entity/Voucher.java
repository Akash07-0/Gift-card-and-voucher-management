package com.example.voucher.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Integer maxUsage;

    @Column(nullable = false)
    private Integer currentUsage = 0;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherScope scope = VoucherScope.SHOP_ONLY;

    @Column(name = "min_purchase_amount")
    private Double minPurchaseAmount;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "terms_and_conditions", length = 2000)
    private String termsAndConditions;

    @Column(name = "promotion_name")
    private String promotionName;

    @Column(nullable = false, length = 10)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType = DiscountType.FIXED_AMOUNT;


    public Voucher() {}

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public Double getDiscount() { return discount; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public Integer getMaxUsage() { return maxUsage; }
    public Integer getCurrentUsage() { return currentUsage; }
    public boolean isActive() { return active; }
    public User getCreatedBy() { return createdBy; }
    public Shop getShop() { return shop; }
    public VoucherScope getScope() { return scope; }
    public Double getMinPurchaseAmount() { return minPurchaseAmount; }
    public Double getMaxDiscount() { return maxDiscount; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public String getPromotionName() { return promotionName; }
    public String getCurrency() { return currency; }
    public DiscountType getDiscountType() { return discountType; }

    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setMaxUsage(Integer maxUsage) { this.maxUsage = maxUsage; }
    public void setCurrentUsage(Integer currentUsage) { this.currentUsage = currentUsage; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setShop(Shop shop) { this.shop = shop; }
    public void setScope(VoucherScope scope) { this.scope = scope; }
    public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
}
