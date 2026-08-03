package com.bharat.auth.dto.response;

import com.bharat.auth.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private boolean emailVerified;
    private boolean enabled;
    private boolean accountNonLocked;
}
