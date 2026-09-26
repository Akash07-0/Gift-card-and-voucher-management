package com.example.voucher.service;

import com.example.voucher.dto.MerchantAnalyticsResponse;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.User;
import com.example.voucher.entity.Voucher;
import com.example.voucher.repository.PurchaseRepository;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantAnalyticsService {

    private final VoucherRepository voucherRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

    public MerchantAnalyticsService(VoucherRepository voucherRepository, ShopRepository shopRepository, UserRepository userRepository, PurchaseRepository purchaseRepository) {
        this.voucherRepository = voucherRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public MerchantAnalyticsResponse getAnalytics(String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        List<Voucher> shopVouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getShop() != null && v.getShop().getId().equals(shop.getId()))
                .collect(Collectors.toList());

        long totalVouchers = shopVouchers.size();
        long activeVouchers = shopVouchers.stream().filter(Voucher::isActive).count();
        long expiredVouchers = shopVouchers.stream().filter(v -> v.getExpiryDate().isBefore(LocalDate.now())).count();
        
        long redeemedVouchers = shopVouchers.stream().mapToLong(Voucher::getCurrentUsage).sum();
        double totalDiscountGiven = shopVouchers.stream()
                .mapToDouble(v -> v.getDiscount() * v.getCurrentUsage())
                .sum();

        long totalPurchases = purchaseRepository.findByShopId(shop.getId()).size();

        // Note: For full accuracy on successful/failed redemptions, a link between Redemption and Shop/Voucher needs tracking. 
        // For now using proxy metrics for the UI requirement.
        long successfulRedemptions = redeemedVouchers;
        long failedRedemptions = 0; // Requires deeper logging

        MerchantAnalyticsResponse response = new MerchantAnalyticsResponse();
        response.setTotalVouchers(totalVouchers);
        response.setActiveVouchers(activeVouchers);
        response.setExpiredVouchers(expiredVouchers);
        response.setRedeemedVouchers(redeemedVouchers);
        response.setTotalPurchases(totalPurchases);
        response.setSuccessfulRedemptions(successfulRedemptions);
        response.setFailedRedemptions(failedRedemptions);
        response.setTotalDiscountGiven(totalDiscountGiven);

        return response;
    }
}
