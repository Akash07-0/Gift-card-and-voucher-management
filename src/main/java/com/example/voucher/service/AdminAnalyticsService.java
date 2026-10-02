package com.example.voucher.service;

import com.example.voucher.dto.AdminStatsResponse;
import com.example.voucher.dto.TimeSeriesDataPoint;
import com.example.voucher.entity.Role;
import com.example.voucher.entity.Voucher;
import com.example.voucher.entity.GiftCard;
import com.example.voucher.entity.Redemption;
import com.example.voucher.entity.GiftCardRedemption;
import com.example.voucher.entity.Purchase;
import com.example.voucher.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    private final VoucherRepository voucherRepository;
    private final GiftCardRepository giftCardRepository;
    private final RedemptionRepository redemptionRepository;
    private final GiftCardRedemptionRepository giftCardRedemptionRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final PurchaseRepository purchaseRepository;
    private final PartnerBrandRepository partnerBrandRepository;

    public AdminAnalyticsService(
            VoucherRepository voucherRepository,
            GiftCardRepository giftCardRepository,
            RedemptionRepository redemptionRepository,
            GiftCardRedemptionRepository giftCardRedemptionRepository,
            UserRepository userRepository,
            ShopRepository shopRepository,
            PurchaseRepository purchaseRepository,
            PartnerBrandRepository partnerBrandRepository
    ) {
        this.voucherRepository = voucherRepository;
        this.giftCardRepository = giftCardRepository;
        this.redemptionRepository = redemptionRepository;
        this.giftCardRedemptionRepository = giftCardRedemptionRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.purchaseRepository = purchaseRepository;
        this.partnerBrandRepository = partnerBrandRepository;
    }

    public AdminStatsResponse getStats(LocalDateTime startDate, LocalDateTime endDate) {
        AdminStatsResponse response = new AdminStatsResponse();
        
        response.setTotalUsers(userRepository.count());
        response.setTotalCustomers(userRepository.countByRole(Role.CUSTOMER));
        response.setTotalMerchants(userRepository.countByRole(Role.MERCHANT));

        List<Voucher> vouchers;
        List<GiftCard> giftCards;
        List<Redemption> redemptions;
        List<GiftCardRedemption> giftCardRedemptions;
        List<Purchase> purchases;

        if (startDate != null && endDate != null) {
            vouchers = voucherRepository.findByCreatedAtBetween(startDate, endDate);
            giftCards = giftCardRepository.findByCreatedAtBetween(startDate, endDate);
            redemptions = redemptionRepository.findByRedeemedAtBetween(startDate, endDate);
            giftCardRedemptions = giftCardRedemptionRepository.findByRedeemedAtBetween(startDate, endDate);
            purchases = purchaseRepository.findByPurchaseDateBetween(startDate, endDate);
        } else {
            vouchers = voucherRepository.findAll();
            giftCards = giftCardRepository.findAll();
            redemptions = redemptionRepository.findAll();
            giftCardRedemptions = giftCardRedemptionRepository.findAll();
            purchases = purchaseRepository.findAll();
        }

        long activeVouchers = vouchers.stream().filter(Voucher::isActive).count();
        long activeGiftCards = giftCards.stream().filter(GiftCard::isActive).count();

        response.setTotalVouchers(vouchers.size());
        response.setActiveVouchers(activeVouchers);
        response.setInactiveVouchers(vouchers.size() - activeVouchers);

        response.setTotalGiftCards(giftCards.size());
        response.setActiveGiftCards(activeGiftCards);
        response.setInactiveGiftCards(giftCards.size() - activeGiftCards);

        response.setTotalRedemptions(redemptions.size() + giftCardRedemptions.size());
        response.setTotalPurchases(purchases.size());

        double totalPurchaseValue = purchases.stream().mapToDouble(Purchase::getAmount).sum();
        double totalRedeemedValue = giftCardRedemptions.stream().mapToDouble(GiftCardRedemption::getAmount).sum()
                + vouchers.stream().mapToDouble(v -> v.getDiscount() * v.getCurrentUsage()).sum();

        response.setTotalDiscountValue(totalRedeemedValue);
        response.setTotalRewardsIssued(giftCards.size());
        response.setTotalRewardsRedeemed(giftCardRedemptions.size());

        response.setActivePartnerBrands(partnerBrandRepository.count());

        // Charts
        response.setVoucherActivityOverTime(aggregateByDate(vouchers.stream().map(Voucher::getCreatedAt).filter(Objects::nonNull).collect(Collectors.toList())));
        response.setPurchaseActivityOverTime(aggregateByDate(purchases.stream().map(Purchase::getPurchaseDate).filter(Objects::nonNull).collect(Collectors.toList())));
        response.setRedemptionActivityOverTime(aggregateByDate(redemptions.stream().map(Redemption::getRedeemedAt).filter(Objects::nonNull).collect(Collectors.toList())));
        response.setRewardIssuanceOverTime(aggregateByDate(giftCards.stream().map(GiftCard::getCreatedAt).filter(Objects::nonNull).collect(Collectors.toList())));
        response.setRewardRedemptionOverTime(aggregateByDate(giftCardRedemptions.stream().map(GiftCardRedemption::getRedeemedAt).filter(Objects::nonNull).collect(Collectors.toList())));

        Map<String, Long> vStatus = new HashMap<>();
        vStatus.put("Active", activeVouchers);
        vStatus.put("Inactive", vouchers.size() - activeVouchers);
        response.setVoucherStatusDistribution(vStatus);

        Map<String, Long> gStatus = new HashMap<>();
        gStatus.put("Active", activeGiftCards);
        gStatus.put("Inactive", giftCards.size() - activeGiftCards);
        response.setGiftCardStatusDistribution(gStatus);

        return response;
    }

    private List<TimeSeriesDataPoint> aggregateByDate(List<LocalDateTime> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> counts = dates.stream()
                .map(d -> d.format(formatter))
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        
        return counts.entrySet().stream()
                .map(e -> new TimeSeriesDataPoint(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(TimeSeriesDataPoint::getDate))
                .collect(Collectors.toList());
    }
}
