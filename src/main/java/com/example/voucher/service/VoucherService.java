package com.example.voucher.service;

import com.example.voucher.dto.*;
import com.example.voucher.entity.*;
import com.example.voucher.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final RedemptionRepository redemptionRepository;

    public VoucherService(
        VoucherRepository voucherRepository,
        UserRepository userRepository,
        RedemptionRepository redemptionRepository
    ) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.redemptionRepository = redemptionRepository;
    }

    public VoucherResponse create(VoucherRequest request, String adminEmail) {
        if (voucherRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Voucher code already exists");
        }

        User admin = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        Voucher voucher = new Voucher();
        voucher.setCode(request.code());
        voucher.setDescription(request.description());
        voucher.setDiscount(request.discount());
        voucher.setExpiryDate(request.expiryDate());
        voucher.setMaxUsage(request.maxUsage());
        voucher.setCurrentUsage(0);
        voucher.setActive(true);
        voucher.setCreatedBy(admin);

        return VoucherResponse.from(voucherRepository.save(voucher));
    }

    public List<VoucherResponse> getAll() {
        return voucherRepository.findAll().stream().map(VoucherResponse::from).toList();
    }

    public List<VoucherResponse> getAvailable() {
        LocalDate today = LocalDate.now();
        return voucherRepository.findAll().stream()
            .filter(v -> v.isActive())
            .filter(v -> v.getExpiryDate().isAfter(today))
            .filter(v -> v.getCurrentUsage() < v.getMaxUsage())
            .map(VoucherResponse::from)
            .toList();
    }

    public VoucherResponse update(Long id, VoucherRequest request) {
        Voucher voucher = getVoucher(id);
        if (voucherRepository.existsByCode(request.code()) &&
            !voucher.getCode().equals(request.code())) {
            throw new IllegalArgumentException("Voucher code already exists");
        }

        voucher.setCode(request.code());
        voucher.setDescription(request.description());
        voucher.setDiscount(request.discount());
        voucher.setExpiryDate(request.expiryDate());
        voucher.setMaxUsage(request.maxUsage());

        return VoucherResponse.from(voucherRepository.save(voucher));
    }

    public void deactivate(Long id) {
        Voucher voucher = getVoucher(id);
        voucher.setActive(false);
        voucherRepository.save(voucher);
    }

    public Voucher getVoucher(Long id) {
        return voucherRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
    }

    @Transactional
    public RedemptionResponse redeem(String code, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Voucher voucher = voucherRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));

        if (!voucher.isActive()) {
            throw new IllegalStateException("Voucher is inactive");
        }

        if (voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Voucher expired");
        }

        if (voucher.getCurrentUsage() >= voucher.getMaxUsage()) {
            throw new IllegalStateException("Voucher usage limit reached");
        }

        if (redemptionRepository.existsByUserIdAndVoucherId(user.getId(), voucher.getId())) {
            throw new IllegalStateException("Voucher already redeemed by this user");
        }

        Redemption redemption =
            new Redemption(user, voucher, java.time.LocalDateTime.now(), "SUCCESS");

        voucher.setCurrentUsage(voucher.getCurrentUsage() + 1);
        voucherRepository.save(voucher);

        return RedemptionResponse.from(redemptionRepository.save(redemption));
    }

    public List<RedemptionResponse> myHistory(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return redemptionRepository.findByUserIdOrderByRedeemedAtDesc(user.getId())
            .stream().map(RedemptionResponse::from).toList();
    }

    public List<RedemptionResponse> allHistory() {
        return redemptionRepository.findAll().stream()
            .map(RedemptionResponse::from).toList();
    }
}
