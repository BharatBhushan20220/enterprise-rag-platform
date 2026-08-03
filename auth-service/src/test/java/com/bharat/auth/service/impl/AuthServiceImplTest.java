package com.bharat.auth.service.impl;

import com.bharat.auth.dto.request.LoginRequest;
import com.bharat.auth.dto.request.RegisterRequest;
import com.bharat.auth.dto.response.LoginResponse;
import com.bharat.auth.dto.response.RegisterResponse;
import com.bharat.auth.entity.Role;
import com.bharat.auth.entity.User;
import com.bharat.auth.mapper.UserMapper;
import com.bharat.auth.repository.RefreshTokenRepository;
import com.bharat.auth.repository.UserRepository;
import com.bharat.auth.security.TokenBlacklistService;
import com.bharat.auth.security.jwt.JwtProperties;
import com.bharat.auth.security.jwt.JwtService;
import com.bharat.common.exception.ConflictException;
import com.bharat.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Bharat");
        registerRequest.setLastName("Prasad");
        registerRequest.setEmail("bharat@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("bharat@example.com");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Bharat");
        user.setLastName("Prasad");
        user.setEmail("bharat@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
    }

    @Test
    void register_shouldSaveUserAndReturnResponse() {
        RegisterResponse expectedResponse = RegisterResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        RegisterResponse response = authService.register(registerRequest);

        assertThat(response.getEmail()).isEqualTo("bharat@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(user);
    }

    @Test
    void register_shouldThrowConflictExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already registered.");

        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toEntity(any());
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(user.getEmail())).thenReturn("jwt-token");
        when(jwtService.generateRefreshTokenValue(user.getEmail())).thenReturn("refresh-token");
        when(jwtProperties.getExpiration()).thenReturn(86400000L);
        when(jwtProperties.getRefreshExpiration()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);
    }

    @Test
    void login_shouldThrowUnauthorizedExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");

        verify(jwtService, never()).generateAccessToken(anyString());
    }

    @Test
    void login_shouldThrowUnauthorizedExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password.");

        verify(jwtService, never()).generateAccessToken(anyString());
    }
}
