package com.bharat.auth.controller;

import com.bharat.auth.dto.request.UpdateRoleRequest;
import com.bharat.auth.dto.request.UpdateUserStatusRequest;
import com.bharat.auth.dto.response.UserResponse;
import com.bharat.auth.service.AdminUserService;
import com.bharat.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
@Tag(name = "Admin Users", description = "Admin-only user management")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users")
    public ApiResponse<List<UserResponse>> listUsers() {
        return ApiResponse.ok(adminUserService.listUsers());
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "Update user role")
    public ApiResponse<UserResponse> updateRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateRoleRequest request) {
        log.info("Admin updating role for userId={} to {}", userId, request.getRole());
        return ApiResponse.ok(adminUserService.updateRole(userId, request), "Role updated");
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Enable/disable or lock a user")
    public ApiResponse<UserResponse> updateStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("Admin updating status for userId={}", userId);
        return ApiResponse.ok(adminUserService.updateStatus(userId, request), "Status updated");
    }
}
