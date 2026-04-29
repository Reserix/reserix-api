package com.reserix.api.user.dto;

import com.reserix.api.user.entity.User;
import com.reserix.api.user.entity.UserRole;
import com.reserix.api.user.entity.UserStatus;

public record UserResponse(
        Long id,
        String email,
        String username,
        UserRole role,
        UserStatus status
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getStatus()
        );
    }
}
