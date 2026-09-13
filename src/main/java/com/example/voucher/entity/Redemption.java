package com.example.voucher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "redemptions",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_voucher",
        columnNames = {"user_id", "voucher_id"}
    )
)
public class Redemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Column(nullable = false)
    private LocalDateTime redeemedAt;

    @Column(nullable = false)
    private String status;

    public Redemption() {}

    public Redemption(User user, Voucher voucher, LocalDateTime redeemedAt, String status) {
        this.user = user;
        this.voucher = voucher;
        this.redeemedAt = redeemedAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Voucher getVoucher() { return voucher; }
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }
    public void setRedeemedAt(LocalDateTime redeemedAt) { this.redeemedAt = redeemedAt; }
    public void setStatus(String status) { this.status = status; }
}
