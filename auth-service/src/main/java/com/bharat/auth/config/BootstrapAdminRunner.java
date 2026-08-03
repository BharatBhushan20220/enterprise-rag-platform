package com.bharat.auth.config;

import com.bharat.auth.entity.Role;
import com.bharat.auth.entity.User;
import com.bharat.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapAdminRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.bootstrap-admin.enabled:false}")
    private boolean enabled;

    @Value("${auth.bootstrap-admin.email:admin@example.com}")
    private String email;

    @Value("${auth.bootstrap-admin.password:Admin@12345}")
    private String password;

    @Value("${auth.bootstrap-admin.first-name:Platform}")
    private String firstName;

    @Value("${auth.bootstrap-admin.last-name:Admin}")
    private String lastName;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User admin = new User();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setEnabled(true);
        admin.setAccountNonLocked(true);
        userRepository.save(admin);
        log.info("Bootstrap admin user created: {}", email);
    }
}
