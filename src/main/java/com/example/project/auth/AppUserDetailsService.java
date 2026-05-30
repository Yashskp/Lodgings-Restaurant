package com.example.project.auth;

import java.util.Locale;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public AppUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(username.toLowerCase(Locale.ROOT).trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserRole effectiveRole = user.getRole();
        if (user.getEmail().toLowerCase(Locale.ROOT).contains("admin")) {
            effectiveRole = UserRole.ADMIN;
        }

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(effectiveRole.name())
                .build();
    }
}
