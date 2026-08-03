package com.bharat.auth.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Instant expiresAt) {
        cleanup();
        blacklistedTokens.put(token, expiresAt);
    }

    public boolean isBlacklisted(String token) {
        cleanup();
        Instant expiresAt = blacklistedTokens.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }

    private void cleanup() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
