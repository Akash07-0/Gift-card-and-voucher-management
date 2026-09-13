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
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password()),
            Role.CUSTOMER
        );

        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getEmail(), user.getRole().name()),
                                user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new AuthResponse(jwtUtil.generateToken(user.getEmail(), user.getRole().name()),
                                user.getRole().name());
    }
}
