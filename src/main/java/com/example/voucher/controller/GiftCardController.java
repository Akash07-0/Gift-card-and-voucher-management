package com.example.voucher.controller;

import com.example.voucher.dto.GiftCardRedeemRequest;
import com.example.voucher.dto.GiftCardRedemptionResponse;
import com.example.voucher.dto.GiftCardRequest;
import com.example.voucher.dto.GiftCardResponse;
import com.example.voucher.service.GiftCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gift-cards")
public class GiftCardController {

    private final GiftCardService giftCardService;
    private final com.example.voucher.security.JwtUtil jwtUtil;
    private final com.example.voucher.repository.UserRepository userRepository;
    private final com.example.voucher.repository.GiftCardRepository giftCardRepository;

    public GiftCardController(GiftCardService giftCardService, 
                              com.example.voucher.security.JwtUtil jwtUtil, 
                              com.example.voucher.repository.UserRepository userRepository,
                              com.example.voucher.repository.GiftCardRepository giftCardRepository) {
        this.giftCardService = giftCardService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.giftCardRepository = giftCardRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftCardResponse> create(
            @Valid @RequestBody GiftCardRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(giftCardService.create(
                        request,
                        authentication.getName()
                ));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GiftCardResponse>> getAll() {

        return ResponseEntity.ok(
                giftCardService.getAll()
        );
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<GiftCardResponse>> getAvailable(
            Authentication authentication) {

        return ResponseEntity.ok(
                giftCardService.getAvailable(authentication.getName())
        );
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftCardResponse> assign(
            @PathVariable Long id,
            @Valid @RequestBody com.example.voucher.dto.AssignCustomerRequest request) {

        return ResponseEntity.ok(
                giftCardService.assign(id, request.getCustomerId())
        );
    }

    @GetMapping("/{code}/qr")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<java.util.Map<String, String>> generateQrToken(
            @PathVariable String code,
            Authentication authentication) {

        com.example.voucher.entity.User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        com.example.voucher.entity.GiftCard giftCard = giftCardRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Gift card not found"));
                
        if (giftCard.getOwner() == null || !giftCard.getOwner().getId().equals(user.getId())) {
             throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

        String token = jwtUtil.generateQrToken(user.getId(), "GC_" + code);
        
        return ResponseEntity.ok(java.util.Map.of("qrToken", token));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<GiftCardRedemptionResponse>> myHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                giftCardService.myRedemptionHistory(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/redemptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GiftCardRedemptionResponse>> allRedemptions() {

        return ResponseEntity.ok(
                giftCardService.allRedemptions()
        );
    }

    // DEACTIVATE GIFT CARD
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {

        giftCardService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    // ACTIVATE GIFT CARD
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(
            @PathVariable Long id) {

        giftCardService.activate(id);

        return ResponseEntity.noContent().build();
    }
}