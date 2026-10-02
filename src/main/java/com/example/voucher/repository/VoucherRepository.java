package com.example.voucher.repository;

import com.example.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);
    boolean existsByCode(String code);
    long countByActiveTrue();
    List<Voucher> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Voucher> findByShopIdAndCreatedAtBetween(Long shopId, LocalDateTime startDate, LocalDateTime endDate);
}

