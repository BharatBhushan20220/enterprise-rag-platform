package com.bharat.auth.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("q8tcHiRtlAmIew4sB5s0HSBrdP68FF9hGhuFA38RWII=");
        jwtProperties.setExpiration(86400000L);
        jwtProperties.setRefreshExpiration(604800000L);
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void generateToken_shouldReturnNonBlankToken() {
        String token = jwtService.generateToken("user@example.com");

        assertThat(token).isNotBlank();
    }

    @Test
    void extractEmail_shouldReturnSubjectFromValidToken() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
    }

    @Test
    void extractEmail_shouldReturnNullForInvalidToken() {
        assertThat(jwtService.extractEmail("invalid.token.value")).isNull();
    }

    @Test
    void extractEmail_shouldReturnNullForTamperedToken() {
        String token = jwtService.generateToken("user@example.com");
        String tamperedToken = token.substring(0, token.length() - 4) + "xxxx";

        assertThat(jwtService.extractEmail(tamperedToken)).isNull();
    }

    @Test
    void isTokenValid_shouldReturnTrueForMatchingUserAndValidToken() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertThat(jwtService.isTokenValid(token, email)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForMismatchedUsername() {
        String token = jwtService.generateToken("user@example.com");

        assertThat(jwtService.isTokenValid(token, "other@example.com")).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isTokenValid("invalid.token.value", "user@example.com")).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("q8tcHiRtlAmIew4sB5s0HSBrdP68FF9hGhuFA38RWII=");
        jwtProperties.setExpiration(-1000L);
        JwtService expiredJwtService = new JwtService(jwtProperties);

        String token = expiredJwtService.generateToken("user@example.com");

        assertThat(expiredJwtService.isTokenValid(token, "user@example.com")).isFalse();
    }
}
