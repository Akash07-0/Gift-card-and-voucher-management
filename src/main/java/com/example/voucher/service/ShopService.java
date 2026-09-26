package com.example.voucher.service;

import com.example.voucher.dto.ShopRequest;
import com.example.voucher.dto.ShopResponse;
import com.example.voucher.entity.Shop;
import com.example.voucher.entity.ShopStatus;
import com.example.voucher.entity.User;
import com.example.voucher.repository.ShopRepository;
import com.example.voucher.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ShopService(ShopRepository shopRepository, UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShopResponse createOrUpdateShop(String merchantEmail, ShopRequest request) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId()).orElse(new Shop());
        shop.setMerchant(merchant);
        shop.setName(request.getName());
        shop.setLogo(request.getLogo());
        shop.setCategory(request.getCategory());
        shop.setDescription(request.getDescription());
        shop.setAddress(request.getAddress());
        shop.setCity(request.getCity());
        shop.setPhone(request.getPhone());
        shop.setEmail(request.getEmail());
        shop.setWebsite(request.getWebsite());

        if (shop.getId() == null) {
            shop.setStatus(ShopStatus.PENDING);
        }

        return mapToResponse(shopRepository.save(shop));
    }

    public ShopResponse getMyShop(String merchantEmail) {
        User merchant = userRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        Shop shop = shopRepository.findByMerchantId(merchant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        return mapToResponse(shop);
    }

    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopResponse verifyShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        shop.setStatus(ShopStatus.VERIFIED);
        return mapToResponse(shopRepository.save(shop));
    }

    @Transactional
    public ShopResponse suspendShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        shop.setStatus(ShopStatus.SUSPENDED);
        return mapToResponse(shopRepository.save(shop));
    }

    private ShopResponse mapToResponse(Shop shop) {
        ShopResponse response = new ShopResponse();
        response.setId(shop.getId());
        response.setMerchantId(shop.getMerchant().getId());
        response.setName(shop.getName());
        response.setLogo(shop.getLogo());
        response.setCategory(shop.getCategory());
        response.setDescription(shop.getDescription());
        response.setAddress(shop.getAddress());
        response.setCity(shop.getCity());
        response.setPhone(shop.getPhone());
        response.setEmail(shop.getEmail());
        response.setWebsite(shop.getWebsite());
        response.setStatus(shop.getStatus());
        response.setCreatedDate(shop.getCreatedDate());
        return response;
    }
}
