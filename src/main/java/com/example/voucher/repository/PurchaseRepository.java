package com.example.voucher.repository;

import com.example.voucher.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByShopId(Long shopId);
    List<Purchase> findByCustomerId(Long customerId);
}
