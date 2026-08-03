package com.bharat.auth.service;

import com.bharat.auth.dto.request.UpdateRoleRequest;
import com.bharat.auth.dto.request.UpdateUserStatusRequest;
import com.bharat.auth.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {

    List<UserResponse> listUsers();

    UserResponse updateRole(UUID userId, UpdateRoleRequest request);

    UserResponse updateStatus(UUID userId, UpdateUserStatusRequest request);
}
