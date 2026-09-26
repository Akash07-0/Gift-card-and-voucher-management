package com.example.voucher;

import com.example.voucher.repository.RedemptionRepository;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.repository.VoucherRepository;
import com.example.voucher.service.VoucherService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import com.example.voucher.entity.DiscountType;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VoucherServiceTest {

    @Test
    void duplicateVoucherShouldBeRejected() {
        VoucherRepository voucherRepo = mock(VoucherRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        RedemptionRepository redemptionRepo = mock(RedemptionRepository.class);

        when(voucherRepo.existsByCode("SAVE100")).thenReturn(true);

        VoucherService service = new VoucherService(voucherRepo, userRepo, redemptionRepo);

        assertThrows(IllegalArgumentException.class, () ->
            service.create(
                new com.example.voucher.dto.VoucherRequest(
                    "SAVE100", "Test", 100.0, LocalDate.now().plusDays(10), 10, DiscountType.FIXED_AMOUNT, "INR"
                ),
                "admin@voucher.com"
            )
        );
    }
}