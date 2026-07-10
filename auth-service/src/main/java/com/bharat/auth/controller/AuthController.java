package com.bharat.auth.controller;

import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import com.bharat.auth.service.AuthService;
import com.bharat.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {

        RegisterResponse response = authService.register(registerRequest);
        return ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message("User registered successfully.")
                .data(response)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();
    }
}
