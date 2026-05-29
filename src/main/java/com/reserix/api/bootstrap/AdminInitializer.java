package com.reserix.api.bootstrap;

import com.reserix.api.user.entity.UserRole;
import com.reserix.api.user.entity.UserStatus;
import com.reserix.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.reserix.api.user.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createDefaultAdmin() {

        boolean exists = userRepository.existsByRole(UserRole.ADMIN);

        if (exists) {
            return;
        }

        String encodedPassword = passwordEncoder.encode(adminPassword);

        User admin = new User(
                adminEmail,
                "Administrator",
                encodedPassword,
                UserRole.ADMIN,
                UserStatus.ACTIVE
        );

        userRepository.save(admin);

        log.info("Default administrator account created: {}", adminEmail);
    }
}