package com.bharat.auth.dto.request;

import com.bharat.auth.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {

    @NotNull
    private Role role;
}
