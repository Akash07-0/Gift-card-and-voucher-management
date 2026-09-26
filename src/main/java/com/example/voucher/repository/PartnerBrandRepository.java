package com.example.voucher.repository;

import com.example.voucher.entity.PartnerBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerBrandRepository extends JpaRepository<PartnerBrand, Long> {
    List<PartnerBrand> findByActiveTrue();
}
