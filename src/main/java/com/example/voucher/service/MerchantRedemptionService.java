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

    // Simulated OTP storage (In production, use Redis or DB with expiry)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public MerchantRedemptionService(VoucherRepository voucherRepository, ShopRepository shopRepository, UserRepository userRepository, RedemptionRepository redemptionRepository) {
        this.voucherRepository = voucherRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.redemptionRepository = redemptionRepository;
    }

    public MerchantRedemptionVerifyResponse verifyVoucher(MerchantRedemptionVerifyRequest request, String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        if (shop.getStatus() != ShopStatus.VERIFIED) {
            throw new IllegalStateException("Only verified merchants can redeem vouchers.");
        }

        Voucher voucher = voucherRepository.findByCode(request.getVoucherCode())
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
        
        // Scope logic for PARTNER_NETWORK and PLATFORM_WIDE omitted for brevity, assuming valid if PLATFORM_WIDE.

        if (redemptionRepository.existsByUserIdAndVoucherId(request.getCustomerId(), voucher.getId())) {
            throw new IllegalStateException("Voucher already redeemed by this user");
        }

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        MerchantRedemptionVerifyResponse response = new MerchantRedemptionVerifyResponse();
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

        Voucher voucher = voucherRepository.findByCode(request.getVoucherCode()).get();
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
}
