package com.k955.kdex.bootstrap;

import com.k955.kdex.entity.User;
import com.k955.kdex.enums.User.Role;
import com.k955.kdex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.fullName}")
    private String fullName;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(userRepository.existsByEmail(email)) {
            log.info("Admin bootstrap skipped: user already exists");
            return;
        }

        userRepository.save(User.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build()
        );

        log.info("Admin bootstrap completed for {}", email);
    }

}
