package com.example.voucher.controller;

import com.example.voucher.dto.ShopResponse;
import com.example.voucher.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/merchants")
public class AdminMerchantController {

    private final ShopService shopService;

    public AdminMerchantController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShopResponse>> getAllMerchants() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopResponse> verifyMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.verifyShop(id));
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopResponse> suspendMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.suspendShop(id));
    }
}
