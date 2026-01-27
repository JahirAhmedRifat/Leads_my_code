package com.example.jwtToken.domains;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "login_users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private boolean locked = false;
    private int failedAttempt = 0;
    private Long lockTime;

}
