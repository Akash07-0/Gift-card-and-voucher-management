package com.example.voucher.service;

import com.example.voucher.dto.GiftCardRedeemRequest;
import com.example.voucher.dto.GiftCardRedemptionResponse;
import com.example.voucher.dto.GiftCardRequest;
import com.example.voucher.dto.GiftCardResponse;
import com.example.voucher.entity.GiftCard;
import com.example.voucher.entity.GiftCardRedemption;
import com.example.voucher.entity.User;
import com.example.voucher.repository.GiftCardRepository;
import com.example.voucher.repository.GiftCardRedemptionRepository;
import com.example.voucher.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GiftCardService {

        private final GiftCardRepository giftCardRepository;
        private final GiftCardRedemptionRepository giftCardRedemptionRepository;
    private final UserRepository userRepository;

    public GiftCardService(
            GiftCardRepository giftCardRepository,
            GiftCardRedemptionRepository giftCardRedemptionRepository,
            UserRepository userRepository) {

        this.giftCardRepository = giftCardRepository;
        this.giftCardRedemptionRepository = giftCardRedemptionRepository;
        this.userRepository = userRepository;
    }

    public GiftCardResponse create(
            GiftCardRequest request,
            String adminEmail) {

        if (giftCardRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException(
                    "Gift card code already exists");
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Admin not found"));

        GiftCard giftCard = new GiftCard();

        giftCard.setCode(request.code());
        giftCard.setAmount(request.amount());
        giftCard.setBalance(request.amount());
        giftCard.setExpiryDate(request.expiryDate());
        giftCard.setActive(true);
        giftCard.setCreatedBy(admin);

        return GiftCardResponse.from(
                giftCardRepository.save(giftCard));
    }

    public List<GiftCardResponse> getAll() {

        return giftCardRepository.findAll()
                .stream()
                .map(GiftCardResponse::from)
                .toList();
    }

    public List<GiftCardResponse> getAvailable() {

        LocalDate today = LocalDate.now();

        return giftCardRepository.findAll()
                .stream()
                .filter(GiftCard::isActive)
                .filter(card ->
                        card.getExpiryDate().isAfter(today))
                .filter(card ->
                        card.getBalance() > 0)
                .map(GiftCardResponse::from)
                .toList();
    }

    public void deactivate(Long id) {

        GiftCard giftCard = giftCardRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Gift card not found"));

        giftCard.setActive(false);

        giftCardRepository.save(giftCard);
    }

    @Transactional
    public GiftCardResponse redeem(
            GiftCardRedeemRequest request,
            String customerEmail) {

        GiftCard giftCard = giftCardRepository
                .findByCode(request.code())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Gift card not found"));

        if (!giftCard.isActive()) {
            throw new IllegalStateException(
                    "Gift card is inactive");
        }

        if (giftCard.getExpiryDate()
                .isBefore(LocalDate.now())) {

            throw new IllegalStateException(
                    "Gift card expired");
        }

        if (request.amount() > giftCard.getBalance()) {
            throw new IllegalStateException(
                    "Insufficient gift card balance");
        }

        giftCard.setBalance(
                giftCard.getBalance() - request.amount()
        );

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        giftCardRedemptionRepository.save(
                new GiftCardRedemption(
                        giftCard,
                        customer,
                        request.amount(),
                        java.time.LocalDateTime.now()));

        return GiftCardResponse.from(
                giftCardRepository.save(giftCard));
    }

    public List<GiftCardRedemptionResponse> myRedemptionHistory(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return giftCardRedemptionRepository
                .findByUserIdOrderByRedeemedAtDesc(customer.getId())
                .stream()
                .map(GiftCardRedemptionResponse::from)
                .toList();
    }

    public List<GiftCardRedemptionResponse> allRedemptions() {
        return giftCardRedemptionRepository.findAllByOrderByRedeemedAtDesc()
                .stream()
                .map(GiftCardRedemptionResponse::from)
                .toList();
    }
}