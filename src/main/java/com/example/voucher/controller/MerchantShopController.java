package com.example.voucher.controller;

import com.example.voucher.dto.PurchaseRequest;
import com.example.voucher.dto.PurchaseResponse;
import com.example.voucher.dto.ShopRequest;
import com.example.voucher.dto.ShopResponse;
import com.example.voucher.service.PurchaseService;
import com.example.voucher.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class MerchantShopController {

    private final ShopService shopService;
    private final PurchaseService purchaseService;

    public MerchantShopController(ShopService shopService, PurchaseService purchaseService) {
        this.shopService = shopService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/shop")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ShopResponse> getMyShop(Authentication authentication) {
        return ResponseEntity.ok(shopService.getMyShop(authentication.getName()));
    }

    @PutMapping("/shop")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ShopResponse> updateMyShop(Authentication authentication, @RequestBody ShopRequest request) {
        return ResponseEntity.ok(shopService.createOrUpdateShop(authentication.getName(), request));
    }

    @GetMapping("/purchases")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<PurchaseResponse>> getPurchases(Authentication authentication) {
        return ResponseEntity.ok(purchaseService.getPurchasesForShop(authentication.getName()));
    }

    @PostMapping("/purchases")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<PurchaseResponse> createPurchase(@RequestParam String customerEmail, @RequestBody PurchaseRequest request) {
        // In a real flow, this links the purchase to the shop. Shop is taken from the request or the current user.
        return ResponseEntity.ok(purchaseService.createPurchase(customerEmail, request));
    }
}
