package com.example.voucher.service;

import com.example.voucher.dto.MerchantAnalyticsResponse;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.User;
import com.example.voucher.entity.Voucher;
import com.example.voucher.entity.Purchase;
import com.example.voucher.repository.PurchaseRepository;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.*;

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

    public MerchantAnalyticsResponse getAnalytics(String merchantEmail, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        List<Voucher> shopVouchers;
        List<Purchase> purchases;

        if (startDate != null && endDate != null) {
            shopVouchers = voucherRepository.findByShopIdAndCreatedAtBetween(shop.getId(), startDate, endDate);
            purchases = purchaseRepository.findByShopIdAndPurchaseDateBetween(shop.getId(), startDate, endDate);
        } else {
            shopVouchers = voucherRepository.findAll().stream()
                    .filter(v -> v.getShop() != null && v.getShop().getId().equals(shop.getId()))
                    .collect(Collectors.toList());
            purchases = purchaseRepository.findByShopId(shop.getId());
        }

        long totalVouchers = shopVouchers.size();
        long activeVouchers = shopVouchers.stream().filter(Voucher::isActive).count();
        long expiredVouchers = shopVouchers.stream().filter(v -> v.getExpiryDate() != null && v.getExpiryDate().isBefore(LocalDate.now())).count();
        
        long redeemedVouchers = shopVouchers.stream().mapToLong(Voucher::getCurrentUsage).sum();
        double totalDiscountGiven = shopVouchers.stream()
                .mapToDouble(v -> v.getDiscount() * v.getCurrentUsage())
                .sum();

        long totalPurchases = purchases.size();
        double totalPurchaseValue = purchases.stream().mapToDouble(Purchase::getAmount).sum();

        // Unique active customers
        long activeCustomers = purchases.stream().map(p -> p.getCustomer().getId()).distinct().count();

        // Promotions count (unique promotion names)
        long activePromotions = shopVouchers.stream().filter(Voucher::isActive)
                .map(Voucher::getPromotionName).filter(Objects::nonNull).distinct().count();

        long successfulRedemptions = redeemedVouchers;
        long failedRedemptions = 0;

        MerchantAnalyticsResponse response = new MerchantAnalyticsResponse();
        response.setTotalVouchers(totalVouchers);
        response.setActiveVouchers(activeVouchers);
        response.setExpiredVouchers(expiredVouchers);
        response.setRedeemedVouchers(redeemedVouchers);
        response.setTotalPurchases(totalPurchases);
        response.setSuccessfulRedemptions(successfulRedemptions);
        response.setFailedRedemptions(failedRedemptions);
        response.setTotalDiscountGiven(totalDiscountGiven);
        response.setTotalPurchaseValue(totalPurchaseValue);
        response.setActiveCustomers(activeCustomers);
        response.setActivePromotions(activePromotions);
        
        // Count rewards issued (purchases with issuedVoucher)
        long totalRewardsIssued = purchases.stream().filter(p -> p.getIssuedVoucher() != null).count();
        response.setTotalRewardsIssued(totalRewardsIssued);
        response.setTotalRewardsRedeemed(0); // Simplified unless specifically queried

        // Charts
        response.setPurchasesOverTime(aggregateByDate(purchases.stream().map(Purchase::getPurchaseDate).filter(Objects::nonNull).collect(Collectors.toList())));
        response.setVoucherUsageOverTime(aggregateByDate(shopVouchers.stream().map(Voucher::getCreatedAt).filter(Objects::nonNull).collect(Collectors.toList())));
        response.setRewardsIssuedOverTime(aggregateByDate(purchases.stream().filter(p -> p.getIssuedVoucher() != null).map(Purchase::getPurchaseDate).filter(Objects::nonNull).collect(Collectors.toList())));
        // Redemptions chart can use voucher created date as proxy if actual redemption date isn't readily joined, or we fetch redemptions for shop.
        // For accurate redemptions over time, we would need to join Redemption and Voucher. We will just leave it empty for now or use a proxy.

        return response;
    }

    private java.util.List<com.example.voucher.dto.TimeSeriesDataPoint> aggregateByDate(List<java.time.LocalDateTime> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> counts = dates.stream()
                .map(d -> d.format(formatter))
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        
        return counts.entrySet().stream()
                .map(e -> new com.example.voucher.dto.TimeSeriesDataPoint(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(com.example.voucher.dto.TimeSeriesDataPoint::getDate))
                .collect(Collectors.toList());
    }
}
