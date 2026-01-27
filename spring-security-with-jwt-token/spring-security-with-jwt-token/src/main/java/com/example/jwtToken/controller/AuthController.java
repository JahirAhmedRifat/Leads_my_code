package com.example.jwtToken.controller;

import com.example.jwtToken.commonException.EmailNotFoundException;
import com.example.jwtToken.commonException.PasswordWrongException;
import com.example.jwtToken.jwtConfigurations.JwtUtil;
import com.example.jwtToken.domains.User;
import com.example.jwtToken.domains.repository.UserRepository;
import com.example.jwtToken.dto.LoginRequest;
import com.example.jwtToken.dto.LoginResponse;
import com.example.jwtToken.dto.RefreshTokenRequest;
import com.example.jwtToken.dto.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("Your email is wrong"));

        // Check if account is locked
        if (user.isLocked()) {
            long currentTime = System.currentTimeMillis();
            if (user.getLockTime() != null && currentTime > user.getLockTime() + 60_000) {
                user.setLocked(false);
                user.setFailedAttempt(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {
                throw new LockedException("Your account is locked. Try after 1 minute.");
            }
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // successful login → reset failed attempts
            user.setFailedAttempt(0);
            userRepository.save(user);

        } catch (BadCredentialsException e) {
            int attempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(attempts);

            if (attempts >= 3) {
                user.setLocked(true);
                user.setLockTime(System.currentTimeMillis());
            }

            userRepository.save(user);
            throw new PasswordWrongException("Your password is wrong. Attempt " + attempts);
        }

        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return ResponseEntity.ok(new LoginResponse(token, refreshToken, user.getName(), user.getEmail()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String email = jwtUtil.extractEmail(request.getRefreshToken());
        User user = userRepository.findByEmail(email).orElseThrow();

        if (jwtUtil.isTokenExpired(request.getRefreshToken())) {
            return ResponseEntity.status(401).body(
                    new RefreshTokenResponse(null, null, false, "Refresh token expired")
            );
        }

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, newRefreshToken, true, "Token refreshed"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Map<String, String> response = Map.of(
                "email", jwtUtil.extractEmail(token),
                "name", jwtUtil.extractName(token)
        );
        return ResponseEntity.ok(response);
    }
}
