package com.bharat.auth.repository;

import com.bharat.auth.entity.RefreshToken;
import com.bharat.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user = :user and rt.revoked = false")
    int revokeAllForUser(@Param("user") User user);
}
