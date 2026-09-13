package com.example.voucher.controller;

import com.example.voucher.dto.*;
import com.example.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redemptions")
public class RedemptionController {
    private final VoucherService voucherService;

    public RedemptionController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RedemptionResponse> redeem(
        @Valid @RequestBody RedeemRequest request,
        Authentication authentication
    ) {
        return ResponseEntity.ok(
            voucherService.redeem(request.code(), authentication.getName())
        );
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RedemptionResponse>> myHistory(Authentication authentication) {
        return ResponseEntity.ok(voucherService.myHistory(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RedemptionResponse>> allHistory() {
        return ResponseEntity.ok(voucherService.allHistory());
    }
}
