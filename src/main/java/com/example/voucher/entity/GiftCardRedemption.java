package com.example.voucher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_card_redemptions")
public class GiftCardRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gift_card_id", nullable = false)
    private GiftCard giftCard;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "remaining_balance", nullable = false)
    private Double remainingBalance;

    @Column(nullable = false)
    private LocalDateTime redeemedAt;

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    protected GiftCardRedemption() {
    }

    public GiftCardRedemption(GiftCard giftCard, User user, Double amount, Double remainingBalance, LocalDateTime redeemedAt) {
        this.giftCard = giftCard;
        this.user = user;
        this.amount = amount;
        this.remainingBalance = remainingBalance;
        this.redeemedAt = redeemedAt;
        this.currency = giftCard.getCurrency();
    }

    public Long getId() { return id; }
    public GiftCard getGiftCard() { return giftCard; }
    public User getUser() { return user; }
    public Double getAmount() { return amount; }
    public Double getRemainingBalance() { return remainingBalance; }
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
