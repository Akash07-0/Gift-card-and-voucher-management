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

    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setMaxUsage(Integer maxUsage) { this.maxUsage = maxUsage; }
    public void setCurrentUsage(Integer currentUsage) { this.currentUsage = currentUsage; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
