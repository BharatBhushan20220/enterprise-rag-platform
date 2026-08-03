package com.bharat.auth.service.impl;

import com.bharat.auth.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "mail.provider", havingValue = "smtp")
public class SmtpEmailNotificationService implements EmailNotificationService {

    private final JavaMailSender mailSender;
    private final String from;
    private final String appBaseUrl;

    public SmtpEmailNotificationService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:noreply@example.com}") String from,
            @Value("${app.base-url:http://localhost:8080}") String appBaseUrl) {
        this.mailSender = mailSender;
        this.from = from;
        this.appBaseUrl = appBaseUrl;
    }

    @Override
    public void sendEmailVerification(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Verify your email");
        message.setText("Open this link to verify your account: "
                + appBaseUrl + "/api/v1/auth/verify-email?token=" + token);
        mailSender.send(message);
        log.info("Sent verification email to {}", toEmail);
    }

    @Override
    public void sendPasswordReset(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Reset your password");
        message.setText("Use this token with POST /api/v1/auth/reset-password: " + token);
        mailSender.send(message);
        log.info("Sent password reset email to {}", toEmail);
    }
}
