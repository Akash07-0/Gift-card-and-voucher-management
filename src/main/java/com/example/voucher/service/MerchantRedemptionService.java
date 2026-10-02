package com.example.voucher.service;

import com.example.voucher.dto.MerchantOtpVerifyRequest;
import com.example.voucher.dto.MerchantRedemptionResponse;
import com.example.voucher.dto.MerchantRedemptionVerifyRequest;
import com.example.voucher.dto.MerchantRedemptionVerifyResponse;
import com.example.voucher.entity.Redemption;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.ShopStatus;
import com.example.voucher.entity.User;
import com.example.voucher.entity.Voucher;
import com.example.voucher.entity.VoucherScope;
import com.example.voucher.repository.RedemptionRepository;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MerchantRedemptionService {

    private final VoucherRepository voucherRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final RedemptionRepository redemptionRepository;
    private final com.example.voucher.repository.GiftCardRepository giftCardRepository;
    private final com.example.voucher.repository.GiftCardRedemptionRepository giftCardRedemptionRepository;

    // Simulated OTP storage (In production, use Redis or DB with expiry)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public MerchantRedemptionService(VoucherRepository voucherRepository, ShopRepository shopRepository, 
                                     UserRepository userRepository, RedemptionRepository redemptionRepository,
                                     com.example.voucher.repository.GiftCardRepository giftCardRepository,
                                     com.example.voucher.repository.GiftCardRedemptionRepository giftCardRedemptionRepository) {
        this.voucherRepository = voucherRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.redemptionRepository = redemptionRepository;
        this.giftCardRepository = giftCardRepository;
        this.giftCardRedemptionRepository = giftCardRedemptionRepository;
    }

    public MerchantRedemptionVerifyResponse verifyVoucher(MerchantRedemptionVerifyRequest request, String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        if (shop.getStatus() != ShopStatus.VERIFIED) {
            throw new IllegalStateException("Only verified merchants can redeem vouchers.");
        }

        String code = request.getVoucherCode();
        if (code != null && code.startsWith("GC_")) {
            return verifyGiftCard(code.substring(3), request, shop);
        }

        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));

        if (!voucher.isActive()) {
            throw new IllegalStateException("Voucher is inactive");
        }

        if (voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Voucher expired");
        }

        if (voucher.getCurrentUsage() >= voucher.getMaxUsage()) {
            throw new IllegalStateException("Voucher usage limit reached");
        }

        if (voucher.getMinPurchaseAmount() != null && request.getPurchaseAmount() < voucher.getMinPurchaseAmount()) {
            throw new IllegalStateException("Minimum purchase amount not met");
        }

        if (voucher.getScope() == VoucherScope.SHOP_ONLY && !voucher.getShop().getId().equals(shop.getId())) {
            throw new IllegalStateException("Voucher can only be redeemed at the issuing shop");
        }

        if (redemptionRepository.existsByUserIdAndVoucherId(request.getCustomerId(), voucher.getId())) {
            throw new IllegalStateException("Voucher already redeemed by this user");
        }

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        MerchantRedemptionVerifyResponse response = new MerchantRedemptionVerifyResponse();
        response.setType("VOUCHER");
        response.setVoucherId(voucher.getId());
        response.setVoucherCode(voucher.getCode());
        response.setPromotionName(voucher.getPromotionName());
        response.setDiscount(voucher.getDiscount());
        response.setMaxDiscount(voucher.getMaxDiscount());
        response.setCustomerId(customer.getId());
        response.setCustomerName(customer.getName());
        response.setShopId(shop.getId());
        response.setShopName(shop.getName());
        response.setPurchaseAmount(request.getPurchaseAmount());
        response.setMinimumPurchaseAmount(voucher.getMinPurchaseAmount());
        response.setExpiryDate(voucher.getExpiryDate());
        response.setCurrentUsage(voucher.getCurrentUsage());
        response.setMaximumUsage(voucher.getMaxUsage());
        response.setScope(voucher.getScope());
        response.setStatus("VALID");
        response.setOtpRequired(true);

        return response;
    }

    private MerchantRedemptionVerifyResponse verifyGiftCard(String code, MerchantRedemptionVerifyRequest request, Shop shop) {
        com.example.voucher.entity.GiftCard gc = giftCardRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Gift card not found"));
        
        if (!gc.isActive()) {
            throw new IllegalStateException("Gift card is inactive");
        }
        if (gc.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Gift card expired");
        }
        if (gc.getOwner() == null || !gc.getOwner().getId().equals(request.getCustomerId())) {
            throw new IllegalStateException("Unauthorized customer for this gift card");
        }
        if (gc.getBalance() <= 0) {
            throw new IllegalStateException("Gift card has no balance");
        }
        
        // If it's a purchase reward, verify if it can be used at this shop. Usually gift cards can be used 
        // at the partner brand or same shop. For simplicity, allow if it's the same shop or a demo brand.
        if (gc.getRewardRule() != null && !gc.getRewardRule().getShop().getId().equals(shop.getId())) {
            // Depending on business rules, maybe they can only redeem it at the same shop
            // Let's assume shop scope for merchant-issued gift cards unless partner brand is PLATFORM_WIDE
            if (gc.getPartnerBrand() == null) {
                throw new IllegalStateException("Gift card not valid for this shop");
            }
        }

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        MerchantRedemptionVerifyResponse response = new MerchantRedemptionVerifyResponse();
        response.setType("GIFT_CARD");
        response.setVoucherId(gc.getId());
        response.setVoucherCode("GC_" + gc.getCode());
        response.setPromotionName("Gift Card");
        response.setBalance(gc.getBalance());
        response.setCurrency(gc.getCurrency());
        response.setBrandName(gc.getPartnerBrand() != null ? gc.getPartnerBrand().getBrandName() : "");
        response.setCustomerId(customer.getId());
        response.setCustomerName(customer.getName());
        response.setShopId(shop.getId());
        response.setShopName(shop.getName());
        response.setPurchaseAmount(request.getPurchaseAmount());
        response.setExpiryDate(gc.getExpiryDate());
        response.setStatus("VALID");
        response.setOtpRequired(true);
        return response;
    }

    public void requestOtp(String voucherCode, Long customerId) {
        // Generate a 6-digit OTP
        String otp = String.format("%06d", (int)(Math.random() * 1000000));
        // Store it using a key that binds the voucher and customer
        String key = voucherCode + "-" + customerId;
        otpStore.put(key, otp);
        
        // In a real application, send this OTP via SMS/Email to the customer.
        System.out.println("OTP for " + key + " is: " + otp);
    }

    @Transactional
    public MerchantRedemptionResponse verifyOtpAndRedeem(MerchantOtpVerifyRequest request, String merchantEmail) {
        String key = request.getVoucherCode() + "-" + request.getCustomerId();
        String storedOtp = otpStore.get(key);

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // Clean up OTP to prevent reuse
        otpStore.remove(key);

        // Perform all validations again to prevent race conditions
        MerchantRedemptionVerifyRequest verifyReq = new MerchantRedemptionVerifyRequest();
        verifyReq.setVoucherCode(request.getVoucherCode());
        verifyReq.setCustomerId(request.getCustomerId());
        verifyReq.setPurchaseAmount(request.getPurchaseAmount());

        MerchantRedemptionVerifyResponse verification = verifyVoucher(verifyReq, merchantEmail);

        String code = request.getVoucherCode();
        if (code != null && code.startsWith("GC_")) {
            return redeemGiftCard(code.substring(3), request, verification);
        }

        Voucher voucher = voucherRepository.findByCode(code).get();
        User customer = userRepository.findById(request.getCustomerId()).get();
        
        // Calculate final discount applied
        double appliedDiscount = voucher.getDiscount();
        if (voucher.getMaxDiscount() != null && appliedDiscount > voucher.getMaxDiscount()) {
            appliedDiscount = voucher.getMaxDiscount();
        }
        double finalAmount = Math.max(0, request.getPurchaseAmount() - appliedDiscount);

        // Update voucher usage
        voucher.setCurrentUsage(voucher.getCurrentUsage() + 1);
        voucherRepository.save(voucher);

        // Create Redemption record
        Redemption redemption = new Redemption(
                customer,
                voucher,
                LocalDateTime.now(),
                "SUCCESS"
        );
        redemptionRepository.save(redemption);

        MerchantRedemptionResponse response = new MerchantRedemptionResponse();
        response.setSuccess(true);
        response.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        response.setVoucherCode(voucher.getCode());
        response.setPromotionName(voucher.getPromotionName());
        response.setShopName(verification.getShopName());
        response.setCustomerName(customer.getName());
        response.setOriginalAmount(request.getPurchaseAmount());
        response.setDiscountApplied(appliedDiscount);
        response.setFinalAmount(finalAmount);
        response.setRedeemedAt(LocalDateTime.now());

        return response;
    }

    private MerchantRedemptionResponse redeemGiftCard(String code, MerchantOtpVerifyRequest request, MerchantRedemptionVerifyResponse verification) {
        com.example.voucher.entity.GiftCard gc = giftCardRepository.findByCodeForUpdate(code)
                .orElseThrow(() -> new IllegalArgumentException("Gift card not found"));
        User customer = userRepository.findById(request.getCustomerId()).get();
        
        Double redeemAmount = request.getPurchaseAmount(); // Actually this is the amount to redeem
        if (redeemAmount == null || redeemAmount <= 0) {
            throw new IllegalArgumentException("Invalid redemption amount");
        }
        if (redeemAmount > gc.getBalance()) {
            throw new IllegalArgumentException("Insufficient gift card balance");
        }

        gc.setBalance(gc.getBalance() - redeemAmount);
        giftCardRepository.save(gc);

        com.example.voucher.entity.GiftCardRedemption redemption = new com.example.voucher.entity.GiftCardRedemption(
                gc,
                customer,
                redeemAmount,
                gc.getBalance(),
                java.time.LocalDateTime.now()
        );
        giftCardRedemptionRepository.save(redemption);

        MerchantRedemptionResponse response = new MerchantRedemptionResponse();
        response.setSuccess(true);
        response.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        response.setVoucherCode("GC_" + gc.getCode());
        response.setPromotionName("Gift Card");
        response.setShopName(verification.getShopName());
        response.setCustomerName(customer.getName());
        response.setOriginalAmount(redeemAmount); // For GC, originalAmount is the amount we are deducting
        response.setDiscountApplied(redeemAmount); // We treat it as discount applied
        response.setFinalAmount(0.0); // No remaining amount for this specific transaction line item
        response.setRedeemedAt(LocalDateTime.now());

        return response;
    }
}
