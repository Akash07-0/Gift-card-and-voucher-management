package com.example.voucher.repository;

import com.example.voucher.entity.GiftCardRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

public interface GiftCardRedemptionRepository extends JpaRepository<GiftCardRedemption, Long> {
    List<GiftCardRedemption> findByUserIdOrderByRedeemedAtDesc(Long userId);
    List<GiftCardRedemption> findAllByOrderByRedeemedAtDesc();
    List<GiftCardRedemption> findByRedeemedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
