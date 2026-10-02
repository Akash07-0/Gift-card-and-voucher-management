package com.example.voucher.controller;

import com.example.voucher.dto.PurchaseResponse;
import com.example.voucher.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/purchases")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPurchaseController {

    private final PurchaseService purchaseService;

    public CustomerPurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getMyPurchases(Authentication authentication) {
        return ResponseEntity.ok(purchaseService.getMyPurchases(authentication.getName()));
    }
}
