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

    public AdminController(
            VoucherRepository voucherRepository,
            GiftCardRepository giftCardRepository,
            RedemptionRepository redemptionRepository,
            GiftCardRedemptionRepository giftCardRedemptionRepository,
            UserRepository userRepository,
            VoucherService voucherService
    ) {
        this.voucherRepository = voucherRepository;
        this.giftCardRepository = giftCardRepository;
        this.redemptionRepository = redemptionRepository;
        this.giftCardRedemptionRepository = giftCardRedemptionRepository;
        this.userRepository = userRepository;
        this.voucherService = voucherService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        long totalVouchers = voucherRepository.count();
        long activeVouchers = voucherRepository.countByActiveTrue();
        long totalGiftCards = giftCardRepository.count();
        long activeGiftCards = giftCardRepository.countByActiveTrue();
        long voucherRedemptions = redemptionRepository.count();
        long giftCardRedemptions = giftCardRedemptionRepository.count();
        long totalRedemptions = voucherRedemptions + giftCardRedemptions;
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);

        return ResponseEntity.ok(new AdminStatsResponse(
                totalVouchers,
                activeVouchers,
                totalGiftCards,
                activeGiftCards,
                totalRedemptions,
                totalCustomers
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAllByOrderByIdAsc()
                .stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
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
