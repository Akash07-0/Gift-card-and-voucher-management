package com.example.voucher.config;

import com.example.voucher.entity.Role;
import com.example.voucher.entity.User;
import com.example.voucher.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createUsers(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {

        return args -> {

            // Create Admin
            if (!userRepository.existsByEmail("admin@voucher.com")) {

                User admin = new User();

                admin.setName("Admin");
                admin.setEmail("admin@voucher.com");
                admin.setPassword(
                    passwordEncoder.encode("Admin@123")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("Default admin created.");
            }

            // Create Customer
            if (!userRepository.existsByEmail("customer@voucher.com")) {

                User customer = new User();

                customer.setName("Customer");
                customer.setEmail("customer@voucher.com");
                customer.setPassword(
                    passwordEncoder.encode("Customer@123")
                );
                customer.setRole(Role.CUSTOMER);

                userRepository.save(customer);

                System.out.println("Default customer created.");
            }
        };
    }

    @Bean
    CommandLineRunner migrateCurrency(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.update("UPDATE voucher SET currency = 'INR' WHERE currency = 'USD'");
                jdbcTemplate.update("UPDATE gift_card SET currency = 'INR' WHERE currency = 'USD'");
                jdbcTemplate.update("UPDATE redemption SET currency = 'INR' WHERE currency = 'USD'");
                jdbcTemplate.update("UPDATE gift_card_redemption SET currency = 'INR' WHERE currency = 'USD'");
                jdbcTemplate.update("UPDATE purchase SET currency = 'INR' WHERE currency = 'USD'");
                jdbcTemplate.update("UPDATE reward_rule SET currency = 'INR' WHERE currency = 'USD'");
                jdbcTemplate.update("UPDATE partner_brand SET currency = 'INR' WHERE currency = 'USD'");
                System.out.println("Currency migration complete (USD -> INR).");
            } catch (Exception e) {
                System.out.println("Currency migration skipped or failed: " + e.getMessage());
            }
        };
    }
}