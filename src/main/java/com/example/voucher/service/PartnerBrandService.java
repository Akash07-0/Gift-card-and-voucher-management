package com.example.voucher.service;

import com.example.voucher.entity.IntegrationMode;
import com.example.voucher.entity.PartnerBrand;
import com.example.voucher.repository.PartnerBrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerBrandService {
    private final PartnerBrandRepository repository;

    public PartnerBrandService(PartnerBrandRepository repository) {
        this.repository = repository;
    }

    public List<PartnerBrand> getAll() {
        return repository.findAll();
    }

    public PartnerBrand create(PartnerBrand brand) {
        if (brand.getRedemptionMode() == null) {
            brand.setRedemptionMode(IntegrationMode.DEMO);
        }
        return repository.save(brand);
    }

    public PartnerBrand update(Long id, PartnerBrand brand) {
        PartnerBrand existing = repository.findById(id).orElseThrow();
        existing.setBrandName(brand.getBrandName());
        existing.setLogo(brand.getLogo());
        existing.setCurrency(brand.getCurrency());
        existing.setRedemptionMode(brand.getRedemptionMode());
        return repository.save(existing);
    }

    public void activate(Long id) {
        PartnerBrand brand = repository.findById(id).orElseThrow();
        brand.setActive(true);
        repository.save(brand);
    }

    public void deactivate(Long id) {
        PartnerBrand brand = repository.findById(id).orElseThrow();
        brand.setActive(false);
        repository.save(brand);
    }
}
