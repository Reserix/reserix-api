package com.reserix.api.user.dto;

import com.reserix.api.user.entity.User;
import com.reserix.api.user.entity.UserRole;
import com.reserix.api.user.entity.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String username,
        UserRole role,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
