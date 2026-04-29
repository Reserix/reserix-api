package com.reserix.api.user.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
