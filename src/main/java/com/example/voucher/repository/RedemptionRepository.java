package com.example.voucher.repository;

import com.example.voucher.entity.Redemption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);
    List<Redemption> findByUserIdOrderByRedeemedAtDesc(Long userId);
}
