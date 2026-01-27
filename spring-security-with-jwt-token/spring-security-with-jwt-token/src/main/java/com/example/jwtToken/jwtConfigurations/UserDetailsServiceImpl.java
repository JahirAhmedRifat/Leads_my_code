package com.example.jwtToken.jwtConfigurations;

import com.example.jwtToken.domains.User;
import com.example.jwtToken.domains.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isLocked()) {
            long currentTime = System.currentTimeMillis();
            if (user.getLockTime() != null && currentTime > user.getLockTime() + 60_000) {
                // Unlock after 1 minute
                user.setLocked(false);
                user.setFailedAttempt(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {
                throw new LockedException("Your account is locked. Try after 1 minute.");
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
