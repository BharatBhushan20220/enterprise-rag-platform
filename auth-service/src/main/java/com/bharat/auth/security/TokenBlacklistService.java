package com.bharat.auth.security;

import java.time.Instant;

public interface TokenBlacklistService {

    void blacklist(String token, Instant expiresAt);

    boolean isBlacklisted(String token);
}
