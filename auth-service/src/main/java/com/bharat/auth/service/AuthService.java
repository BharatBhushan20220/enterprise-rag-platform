package com.bharat.auth.service;

import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RefreshTokenRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import com.bharat.auth.dto.response.UserResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshTokenRequest request);

    void logout(String accessToken, RefreshTokenRequest request);

    UserResponse me(String email);
}
