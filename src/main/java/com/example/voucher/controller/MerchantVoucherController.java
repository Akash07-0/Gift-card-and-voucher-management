package com.example.voucher.controller;

import com.example.voucher.dto.MerchantVoucherRequest;
import com.example.voucher.dto.VoucherResponse;
import com.example.voucher.service.MerchantVoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/vouchers")
public class MerchantVoucherController {

    private final MerchantVoucherService merchantVoucherService;

    public MerchantVoucherController(MerchantVoucherService merchantVoucherService) {
        this.merchantVoucherService = merchantVoucherService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<VoucherResponse> createVoucher(Authentication authentication, @RequestBody MerchantVoucherRequest request) {
        return ResponseEntity.ok(merchantVoucherService.createVoucher(request, authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<VoucherResponse>> getMyVouchers(Authentication authentication) {
        return ResponseEntity.ok(merchantVoucherService.getMyVouchers(authentication.getName()));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<VoucherResponse> activateVoucher(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(merchantVoucherService.toggleVoucherStatus(id, authentication.getName(), true));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<VoucherResponse> deactivateVoucher(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(merchantVoucherService.toggleVoucherStatus(id, authentication.getName(), false));
    }
}
