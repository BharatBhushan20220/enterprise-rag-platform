package com.bharat.auth.service;

public interface EmailNotificationService {

    void sendEmailVerification(String toEmail, String token);

    void sendPasswordReset(String toEmail, String token);
}
