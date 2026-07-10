package com.bharat.auth.service.impl;

import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import com.bharat.auth.entity.User;
import com.bharat.auth.mapper.UserMapper;
import com.bharat.auth.repository.UserRepository;
import com.bharat.auth.security.jwt.JwtProperties;
import com.bharat.auth.security.jwt.JwtService;
import com.bharat.auth.service.AuthService;
import com.bharat.common.exception.ResourceNotFoundException;
import com.bharat.common.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;


    @Override
    public RegisterResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceNotFoundException("Email already registered.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saveUser = userRepository.save(user);

        return userMapper.toResponse(saveUser);
    }

    @Override
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!passwordMatches){
            throw new UnauthorizedException("Invalid email or password.");
        }

        String accessToken = jwtService.generateToken(
                user.getEmail()
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .build();
    }
}
