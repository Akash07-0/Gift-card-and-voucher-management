package com.example.voucher.controller;

import com.example.voucher.dto.MerchantAnalyticsResponse;
import com.example.voucher.service.MerchantAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchant/analytics")
public class MerchantAnalyticsController {

    private final MerchantAnalyticsService merchantAnalyticsService;

    public MerchantAnalyticsController(MerchantAnalyticsService merchantAnalyticsService) {
        this.merchantAnalyticsService = merchantAnalyticsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<MerchantAnalyticsResponse> getAnalytics(
            Authentication authentication,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate
    ) {
        return ResponseEntity.ok(merchantAnalyticsService.getAnalytics(authentication.getName(), startDate, endDate));
    }
}
