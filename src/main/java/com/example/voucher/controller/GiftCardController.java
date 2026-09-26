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

    public GiftCardController(GiftCardService giftCardService) {
        this.giftCardService = giftCardService;
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
    public ResponseEntity<List<GiftCardResponse>> getAvailable() {

        return ResponseEntity.ok(
                giftCardService.getAvailable()
        );
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<GiftCardResponse> redeem(
            @Valid @RequestBody GiftCardRedeemRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                giftCardService.redeem(
                        request,
                        authentication.getName()
                )
        );
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