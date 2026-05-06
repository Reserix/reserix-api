package com.reserix.api.user.dto;

import com.reserix.api.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateByAdminRequest(
        @Email
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "password is required")
        String password,

        @NotBlank(message = "role is required")
        UserRole role
) {
}
