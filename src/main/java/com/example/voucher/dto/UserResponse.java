package com.example.voucher.dto;

import com.example.voucher.entity.Role;
import com.example.voucher.entity.User;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
