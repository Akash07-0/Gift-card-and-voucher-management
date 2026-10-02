package com.example.voucher.repository;

import com.example.voucher.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByShopId(Long shopId);
    List<Purchase> findByCustomerId(Long customerId);
    List<Purchase> findByPurchaseDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Purchase> findByShopIdAndPurchaseDateBetween(Long shopId, LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByOrderId(String orderId);
}
