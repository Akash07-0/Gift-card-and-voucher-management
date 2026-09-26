package com.example.voucher.service;

import com.example.voucher.dto.MerchantVoucherRequest;
import com.example.voucher.dto.VoucherResponse;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.ShopStatus;
import com.example.voucher.entity.User;
import com.example.voucher.entity.Voucher;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantVoucherService {

    private final VoucherRepository voucherRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public MerchantVoucherService(VoucherRepository voucherRepository, ShopRepository shopRepository, UserRepository userRepository) {
        this.voucherRepository = voucherRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public VoucherResponse createVoucher(MerchantVoucherRequest request, String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        if (shop.getStatus() != ShopStatus.VERIFIED) {
            throw new IllegalStateException("Only verified merchants can create vouchers.");
        }

        if (voucherRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Voucher code already exists");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setDiscount(request.getDiscount());
        voucher.setExpiryDate(request.getExpiryDate());
        voucher.setMaxUsage(request.getMaxUsage());
        voucher.setCurrentUsage(0);
        voucher.setActive(true);
        voucher.setCreatedBy(merchant);

        // Merchant specific fields
        voucher.setShop(shop);
        voucher.setScope(request.getScope());
        voucher.setMinPurchaseAmount(request.getMinPurchaseAmount());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setTermsAndConditions(request.getTermsAndConditions());
        voucher.setPromotionName(request.getPromotionName());

        return VoucherResponse.from(voucherRepository.save(voucher));
    }

    public List<VoucherResponse> getMyVouchers(String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        // Note: For simplicity, filtering in memory. In a real app, use a custom repository method.
        return voucherRepository.findAll().stream()
                .filter(v -> v.getShop() != null && v.getShop().getId().equals(shop.getId()))
                .map(VoucherResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public VoucherResponse toggleVoucherStatus(Long voucherId, String merchantEmail, boolean status) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));
        
        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));

        if (voucher.getShop() == null || !voucher.getShop().getId().equals(shop.getId())) {
            throw new IllegalStateException("You do not own this voucher.");
        }

        voucher.setActive(status);
        return VoucherResponse.from(voucherRepository.save(voucher));
    }
}
