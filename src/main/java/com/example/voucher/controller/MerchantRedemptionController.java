package com.example.voucher.controller;

import com.example.voucher.dto.MerchantOtpVerifyRequest;
import com.example.voucher.dto.MerchantRedemptionResponse;
import com.example.voucher.dto.MerchantRedemptionVerifyRequest;
import com.example.voucher.dto.MerchantRedemptionVerifyResponse;
import com.example.voucher.service.MerchantRedemptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import com.example.voucher.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant/redemptions")
public class MerchantRedemptionController {

    private final MerchantRedemptionService merchantRedemptionService;
    private final JwtUtil jwtUtil;

    public MerchantRedemptionController(MerchantRedemptionService merchantRedemptionService, JwtUtil jwtUtil) {
        this.merchantRedemptionService = merchantRedemptionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantRedemptionVerifyResponse> verifyRedemption(
            Authentication authentication,
            @RequestBody MerchantRedemptionVerifyRequest request) {
        
        return ResponseEntity.ok(merchantRedemptionService.verifyVoucher(request, authentication.getName()));
    }

    @PostMapping("/request-otp")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<Map<String, Object>> requestOtp(
            Authentication authentication,
            @RequestParam String voucherCode,
            @RequestParam Long customerId) {
        
        merchantRedemptionService.requestOtp(voucherCode, customerId);
        
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "expiresInSeconds", 300
        ));
    }

    @PostMapping("/verify-otp")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantRedemptionResponse> verifyOtpAndRedeem(
            Authentication authentication,
            @RequestBody MerchantOtpVerifyRequest request) {
        
        return ResponseEntity.ok(merchantRedemptionService.verifyOtpAndRedeem(request, authentication.getName()));
    }

    @PostMapping("/scan-qr")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantRedemptionVerifyResponse> scanQr(
            Authentication authentication,
            @RequestBody Map<String, String> payload) {
        
        String qrToken = payload.get("qrToken");
        if (qrToken == null || qrToken.isBlank()) {
            throw new IllegalArgumentException("QR token is required");
        }
        
        Claims claims = jwtUtil.decodeQrToken(qrToken);
        String voucherCode = claims.getSubject();
        Long customerId = claims.get("customerId", Long.class);
        
        MerchantRedemptionVerifyRequest request = new MerchantRedemptionVerifyRequest();
        request.setVoucherCode(voucherCode);
        request.setCustomerId(customerId);
        
        return ResponseEntity.ok(merchantRedemptionService.verifyVoucher(request, authentication.getName()));
    }
}
