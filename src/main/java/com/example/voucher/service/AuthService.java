package com.example.voucher.service;

import com.example.voucher.dto.*;
import com.example.voucher.entity.*;
import com.example.voucher.repository.UserRepository;
import com.example.voucher.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        String cleanEmail = request.email() != null ? request.email().trim() : "";
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(
            request.name(),
            cleanEmail,
            passwordEncoder.encode(request.password()),
            Role.CUSTOMER
        );

        userRepository.save(user);
        String roleName = user.getRole() != null ? user.getRole().name() : "CUSTOMER";
        return new AuthResponse(jwtUtil.generateToken(user.getEmail(), roleName), roleName);
    }

    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.email() != null ? request.email().trim() : "";
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(cleanEmail, request.password())
        );

        User user = userRepository.findByEmail(cleanEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String roleName = user.getRole() != null ? user.getRole().name() : "CUSTOMER";
        return new AuthResponse(jwtUtil.generateToken(user.getEmail(), roleName), roleName);
    }
}
