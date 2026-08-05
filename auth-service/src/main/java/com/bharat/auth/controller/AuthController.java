package com.bharat.auth.controller;

import com.bharat.auth.dto.request.ForgotPasswordRequest;
import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RefreshTokenRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.request.ResendVerificationRequest;
import com.bharat.auth.dto.request.ResetPasswordRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import com.bharat.auth.dto.response.UserResponse;
import com.bharat.auth.service.AuthService;
import com.bharat.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Registration, login, tokens, and account recovery")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register request for email={}", registerRequest.getEmail());
        return ApiResponse.ok(authService.register(registerRequest), "User registered successfully.");
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT tokens")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());
        return ApiResponse.ok(authService.login(request), "Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refresh(request), "Token refreshed");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke tokens")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody(required = false) RefreshTokenRequest request) {
        String accessToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        }
        authService.logout(accessToken, request == null ? new RefreshTokenRequest() : request);
        return ApiResponse.ok(null, "Logged out successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "Current authenticated user profile")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<UserResponse> me(Authentication authentication) {
        return ApiResponse.ok(authService.me(authentication.getName()), "Current user profile");
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with token")
    public ApiResponse<Void> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ApiResponse.ok(null, "Email verified successfully");
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification link")
    public ApiResponse<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ApiResponse.ok(null, "Verification email sent");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset token")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok(null, "If the email exists, a reset token has been sent");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok(null, "Password reset successfully");
    }
}
