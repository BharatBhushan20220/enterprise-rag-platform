package com.bharat.auth.service;

import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import org.apache.coyote.BadRequestException;

public interface AuthService {

    RegisterResponse register(RegisterRequest request) ;
    LoginResponse login(LoginRequest request) ;
}
