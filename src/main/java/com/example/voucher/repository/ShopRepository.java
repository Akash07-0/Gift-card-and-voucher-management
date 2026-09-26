package com.example.voucher.repository;

import com.example.voucher.entity.Shop;
import com.example.voucher.entity.ShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByMerchantId(Long merchantId);
    List<Shop> findByStatus(ShopStatus status);
}
