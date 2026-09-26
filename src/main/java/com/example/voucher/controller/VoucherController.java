package com.example.voucher.controller;

import com.example.voucher.dto.*;
import com.example.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.voucher.security.JwtUtil;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.entity.User;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public VoucherController(VoucherService voucherService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.voucherService = voucherService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VoucherResponse> create(
            @Valid @RequestBody VoucherRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(voucherService.create(request, authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VoucherResponse>> getAll() {
        return ResponseEntity.ok(voucherService.getAll());
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<VoucherResponse>> getAvailable() {
        return ResponseEntity.ok(voucherService.getAvailable());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VoucherResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody VoucherRequest request
    ) {
        return ResponseEntity.ok(voucherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        voucherService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ACTIVATE VOUCHER
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VoucherResponse> activate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                voucherService.activate(id)
        );
    }

    @GetMapping("/{code}/qr")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, String>> generateQrToken(
            @PathVariable String code,
            Authentication authentication
    ) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        String token = jwtUtil.generateQrToken(user.getId(), code);
        return ResponseEntity.ok(Map.of("qrToken", token));
    }
}