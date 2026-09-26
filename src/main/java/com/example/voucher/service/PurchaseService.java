package com.example.voucher.service;

import com.example.voucher.dto.PurchaseRequest;
import com.example.voucher.dto.PurchaseResponse;
import com.example.voucher.entity.Purchase;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.User;
import com.example.voucher.repository.PurchaseRepository;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.RewardRuleRepository;
import com.example.voucher.repository.GiftCardRepository;
import com.example.voucher.entity.GiftCard;
import com.example.voucher.entity.RewardRule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final RewardRuleRepository rewardRuleRepository;
    private final GiftCardRepository giftCardRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, 
                           ShopRepository shopRepository, 
                           UserRepository userRepository,
                           RewardRuleRepository rewardRuleRepository,
                           GiftCardRepository giftCardRepository) {
        this.purchaseRepository = purchaseRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.rewardRuleRepository = rewardRuleRepository;
        this.giftCardRepository = giftCardRepository;
    }

    @Transactional
    public PurchaseResponse createPurchase(String customerEmail, PurchaseRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        Purchase purchase = new Purchase();
        purchase.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        purchase.setCustomer(customer);
        purchase.setShop(shop);
        purchase.setAmount(request.getAmount());
        if (request.getCurrency() != null && !request.getCurrency().isEmpty()) {
            purchase.setCurrency(request.getCurrency());
        }

        purchase = purchaseRepository.save(purchase);
        evaluateRewardRules(purchase);

        return mapToResponse(purchase);
    }

    private void evaluateRewardRules(Purchase purchase) {
        List<RewardRule> rules = rewardRuleRepository.findByShopIdAndActiveTrue(purchase.getShop().getId());
        for (RewardRule rule : rules) {
            if (purchase.getAmount() >= rule.getMinimumPurchaseAmount()) {
                GiftCard reward = new GiftCard();
                reward.setCode("RWD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                reward.setAmount(rule.getRewardAmount());
                reward.setBalance(rule.getRewardAmount());
                reward.setCurrency(rule.getCurrency());
                reward.setExpiryDate(java.time.LocalDate.now().plusMonths(6));
                reward.setCreatedBy(purchase.getCustomer()); // Issue to customer
                reward.setPartnerBrand(rule.getRewardBrand());
                giftCardRepository.save(reward);
                break; // Only apply one rule for simplicity
            }
        }
    }

    public List<PurchaseResponse> getPurchasesForShop(String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        return purchaseRepository.findByShopId(shop.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PurchaseResponse> getMyPurchases(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return purchaseRepository.findByCustomerId(customer.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PurchaseResponse mapToResponse(Purchase purchase) {
        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getId());
        response.setOrderId(purchase.getOrderId());
        response.setCustomerId(purchase.getCustomer().getId());
        response.setShopId(purchase.getShop().getId());
        response.setAmount(purchase.getAmount());
        response.setCurrency(purchase.getCurrency());
        response.setPurchaseDate(purchase.getPurchaseDate());
        if (purchase.getIssuedVoucher() != null) {
            response.setIssuedVoucherId(purchase.getIssuedVoucher().getId());
        }
        return response;
    }
}
