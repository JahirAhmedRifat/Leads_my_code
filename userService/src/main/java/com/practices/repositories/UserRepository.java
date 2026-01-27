package com.practices.repositories;

import com.practices.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByUserId(String userId);
//    Optional<User> findByUserIdIgnoreCase(String userId);
//    boolean existsByUserIdIgnoreCase(String userId);

    Optional<User> findByUserIdIgnoreCaseAndIsDeletedFalse(String userId);
    boolean existsByUserIdIgnoreCaseAndIsDeletedFalse(String userId);
    List<User> findAllByIsDeletedFalse();
    Optional<User> findByRole(String role);

}
