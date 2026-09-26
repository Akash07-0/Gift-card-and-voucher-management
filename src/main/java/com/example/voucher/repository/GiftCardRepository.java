package com.example.voucher.repository;

import com.example.voucher.entity.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {

    boolean existsByCode(String code);

    Optional<GiftCard> findByCode(String code);

    long countByActiveTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select card from GiftCard card where card.code = :code")
    Optional<GiftCard> findByCodeForUpdate(String code);
}