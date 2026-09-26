package com.example.voucher.repository;

import com.example.voucher.entity.RewardRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRuleRepository extends JpaRepository<RewardRule, Long> {
    List<RewardRule> findByShopIdAndActiveTrue(Long shopId);
}
