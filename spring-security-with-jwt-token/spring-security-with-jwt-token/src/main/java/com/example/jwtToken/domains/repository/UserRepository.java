package com.example.jwtToken.domains.repository;

import com.example.jwtToken.domains.User;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Id> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
