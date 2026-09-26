package com.example.voucher.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

                // CORS preflight
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                )
                .permitAll()

                // Frontend / static
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/assets/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                )
                .permitAll()

                // Authentication
                .requestMatchers("/api/auth/**")
                .permitAll()

                // Swagger
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                )
                .permitAll()

                // Health / error
                .requestMatchers(
                    "/health",
                    "/error"
                )
                .permitAll()

                // Admin
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                // Merchant
                .requestMatchers("/api/merchant/**")
                .hasRole("MERCHANT")

                // Available vouchers / gift cards
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/vouchers/available",
                    "/api/gift-cards/available"
                )
                .hasAnyRole("ADMIN", "CUSTOMER")

                // Customer redemption
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/redemptions",
                    "/api/gift-cards/redeem"
                )
                .hasRole("CUSTOMER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/redemptions/my-history",
                    "/api/gift-cards/my-history"
                )
                .hasRole("CUSTOMER")

                // Admin vouchers
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/vouchers"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/vouchers"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/vouchers/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/vouchers/**"
                )
                .hasRole("ADMIN")

                // Admin gift cards
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/gift-cards",
                    "/api/gift-cards/redemptions"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/gift-cards"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/gift-cards/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/gift-cards/**"
                )
                .hasRole("ADMIN")

                // Admin redemptions
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/redemptions"
                )
                .hasRole("ADMIN")

                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",

                // Your current Vite frontend
                "http://localhost:5175",
                "http://127.0.0.1:5175",

                "http://localhost:5500",
                "http://127.0.0.1:5500"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}