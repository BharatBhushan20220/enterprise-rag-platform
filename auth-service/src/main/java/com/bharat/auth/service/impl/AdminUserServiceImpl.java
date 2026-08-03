package com.bharat.auth.service.impl;

import com.bharat.auth.dto.request.UpdateRoleRequest;
import com.bharat.auth.dto.request.UpdateUserStatusRequest;
import com.bharat.auth.dto.response.UserResponse;
import com.bharat.auth.entity.User;
import com.bharat.auth.mapper.UserMapper;
import com.bharat.auth.repository.UserRepository;
import com.bharat.auth.service.AdminUserService;
import com.bharat.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse updateRole(UUID userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(request.getRole());
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(request.getEnabled());
        user.setAccountNonLocked(request.getAccountNonLocked());
        return userMapper.toUserResponse(user);
    }
}
