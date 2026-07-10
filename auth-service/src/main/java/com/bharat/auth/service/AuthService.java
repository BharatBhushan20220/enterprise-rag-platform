package com.bharat.auth.service;

import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);
}
