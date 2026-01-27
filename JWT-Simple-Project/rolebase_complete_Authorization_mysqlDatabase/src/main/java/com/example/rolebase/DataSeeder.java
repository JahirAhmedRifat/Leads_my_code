package com.example.rolebase;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), Set.of("ROLE_ADMIN")));
            userRepository.save(new User("user", passwordEncoder.encode("user123"), Set.of("ROLE_USER")));
        };
    }
}

