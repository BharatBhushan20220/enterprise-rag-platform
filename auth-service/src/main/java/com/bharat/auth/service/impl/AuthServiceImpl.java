package com.bharat.auth.service.impl;

import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RefreshTokenRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import com.bharat.auth.dto.response.UserResponse;
import com.bharat.auth.entity.RefreshToken;
import com.bharat.auth.entity.User;
import com.bharat.auth.mapper.UserMapper;
import com.bharat.auth.repository.RefreshTokenRepository;
import com.bharat.auth.repository.UserRepository;
import com.bharat.auth.security.TokenBlacklistService;
import com.bharat.auth.security.jwt.JwtProperties;
import com.bharat.auth.security.jwt.JwtService;
import com.bharat.auth.service.AuthService;
import com.bharat.common.exception.ConflictException;
import com.bharat.common.exception.ResourceNotFoundException;
import com.bharat.common.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saveUser = userRepository.save(user);
        return userMapper.toResponse(saveUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        return issueTokens(user);
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            stored.setRevoked(true);
            throw new UnauthorizedException("Refresh token expired");
        }

        if (!jwtService.isTokenValid(request.getRefreshToken(), stored.getUser().getEmail())) {
            stored.setRevoked(true);
            throw new UnauthorizedException("Invalid refresh token");
        }

        stored.setRevoked(true);
        return issueTokens(stored.getUser());
    }

    @Override
    public void logout(String accessToken, RefreshTokenRequest request) {
        if (accessToken != null && !accessToken.isBlank()) {
            tokenBlacklistService.blacklist(accessToken, jwtService.extractExpiration(accessToken));
        }
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                    .ifPresent(token -> token.setRevoked(true));
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }

    private LoginResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtService.generateRefreshTokenValue(user.getEmail());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .build();
    }
}
