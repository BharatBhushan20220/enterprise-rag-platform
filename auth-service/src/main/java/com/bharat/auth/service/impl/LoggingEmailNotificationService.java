package com.bharat.auth.service.impl;

import com.bharat.auth.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "mail.provider", havingValue = "logging", matchIfMissing = true)
public class LoggingEmailNotificationService implements EmailNotificationService {

    private final String appBaseUrl;

    public LoggingEmailNotificationService(@Value("${app.base-url:http://localhost:8080}") String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    @Override
    public void sendEmailVerification(String toEmail, String token) {
        log.info("EMAIL VERIFICATION to={} link={}/api/v1/auth/verify-email?token={}",
                toEmail, appBaseUrl, token);
    }

    @Override
    public void sendPasswordReset(String toEmail, String token) {
        log.info("PASSWORD RESET to={} token={} reset-path=/api/v1/auth/reset-password",
                toEmail, token);
    }
}
