package com.example.voucher.repository;

import com.example.voucher.entity.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {

    boolean existsByCode(String code);

    Optional<GiftCard> findByCode(String code);
}