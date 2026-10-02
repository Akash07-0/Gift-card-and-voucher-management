package com.example.voucher.dto;

import jakarta.validation.constraints.NotNull;

public class AssignCustomerRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
