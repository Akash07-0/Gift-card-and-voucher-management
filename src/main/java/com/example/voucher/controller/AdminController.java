package com.example.voucher.controller;

import com.example.voucher.dto.AdminStatsResponse;
import com.example.voucher.dto.UserResponse;
import com.example.voucher.entity.Role;
import com.example.voucher.repository.GiftCardRedemptionRepository;
import com.example.voucher.repository.GiftCardRepository;
import com.example.voucher.repository.RedemptionRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.VoucherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.voucher.service.VoucherService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final VoucherRepository voucherRepository;
    private final GiftCardRepository giftCardRepository;
    private final RedemptionRepository redemptionRepository;
    private final GiftCardRedemptionRepository giftCardRedemptionRepository;
    private final UserRepository userRepository;

    private final VoucherService voucherService;
    private final com.example.voucher.service.AdminAnalyticsService adminAnalyticsService;

    public AdminController(
            VoucherRepository voucherRepository,
            GiftCardRepository giftCardRepository,
            RedemptionRepository redemptionRepository,
            GiftCardRedemptionRepository giftCardRedemptionRepository,
            UserRepository userRepository,
            VoucherService voucherService,
            com.example.voucher.service.AdminAnalyticsService adminAnalyticsService
    ) {
        this.voucherRepository = voucherRepository;
        this.giftCardRepository = giftCardRepository;
        this.redemptionRepository = redemptionRepository;
        this.giftCardRedemptionRepository = giftCardRedemptionRepository;
        this.userRepository = userRepository;
        this.voucherService = voucherService;
        this.adminAnalyticsService = adminAnalyticsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats(
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate
    ) {
        return ResponseEntity.ok(adminAnalyticsService.getStats(startDate, endDate));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAllByOrderByIdAsc()
                .stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<com.example.voucher.dto.PurchaseResponse>> getAllPurchases(
            @org.springframework.beans.factory.annotation.Autowired com.example.voucher.service.PurchaseService purchaseService) {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }

    @PutMapping("/vouchers/{id}/activate")
    public ResponseEntity<Void> activateVoucher(@PathVariable Long id) {
        voucherService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/vouchers/{id}/deactivate")
    public ResponseEntity<Void> deactivateVoucher(@PathVariable Long id) {
        voucherService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
