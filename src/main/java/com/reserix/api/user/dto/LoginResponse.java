package com.reserix.api.user.dto;

import com.reserix.api.user.entity.UserRole;

public record LoginResponse(
        String accessToken,
        String tokenType,
        UserRole role
) {
}
