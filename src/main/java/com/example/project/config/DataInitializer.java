package com.example.project.config;

import com.example.project.auth.AppUser;
import com.example.project.auth.AppUserRepository;
import com.example.project.auth.UserRole;
import java.util.Locale;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@lodgings.com";
    private static final String ADMIN_PASSWORD = "admin12345";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String email = ADMIN_EMAIL.toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            return;
        }

        AppUser admin = new AppUser();
        admin.setFullName("System Administrator");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
    }
}
