package com.example.voucher.repository;

import com.example.voucher.entity.Redemption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);
    List<Redemption> findByUserIdOrderByRedeemedAtDesc(Long userId);
    List<Redemption> findByRedeemedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Redemption> findByVoucher_ShopIdAndRedeemedAtBetween(Long shopId, LocalDateTime startDate, LocalDateTime endDate);
}
