package com.bharat.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String CLAIM_TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access";
    public static final String REFRESH_TOKEN = "refresh";

    private final JwtProperties jwtProperties;

    public String generateAccessToken(String email) {
        return buildToken(email, jwtProperties.getExpiration(), ACCESS_TOKEN);
    }

    public String generateRefreshTokenValue(String email) {
        return buildToken(email, jwtProperties.getRefreshExpiration(), REFRESH_TOKEN);
    }

    /** @deprecated use {@link #generateAccessToken(String)} */
    @Deprecated
    public String generateToken(String email) {
        return generateAccessToken(email);
    }

    public String extractEmail(String jwtToken) {
        try {
            return extractClaim(jwtToken, Claims::getSubject);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public Instant extractExpiration(String jwtToken) {
        try {
            Date expiration = extractClaim(jwtToken, Claims::getExpiration);
            return expiration.toInstant();
        } catch (JwtException | IllegalArgumentException ex) {
            return Instant.now();
        }
    }

    public boolean isAccessToken(String jwtToken) {
        try {
            String type = extractClaim(jwtToken, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
            return type == null || ACCESS_TOKEN.equals(type);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isTokenValid(String jwtToken, String username) {
        try {
            String email = extractClaim(jwtToken, Claims::getSubject);
            return email != null
                    && email.equals(username)
                    && !isTokenExpired(jwtToken);
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private String buildToken(String email, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(email)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
