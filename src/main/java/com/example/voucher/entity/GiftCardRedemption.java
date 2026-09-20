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

    @Column(nullable = false)
    private LocalDateTime redeemedAt;

    protected GiftCardRedemption() {
    }

    public GiftCardRedemption(GiftCard giftCard, User user, Double amount, LocalDateTime redeemedAt) {
        this.giftCard = giftCard;
        this.user = user;
        this.amount = amount;
        this.redeemedAt = redeemedAt;
    }

    public Long getId() { return id; }
    public GiftCard getGiftCard() { return giftCard; }
    public User getUser() { return user; }
    public Double getAmount() { return amount; }
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
}
