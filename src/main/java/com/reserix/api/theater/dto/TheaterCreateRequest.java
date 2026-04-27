package com.reserix.api.theater.dto;

import jakarta.validation.constraints.NotBlank;

public record TheaterCreateRequest(
        @NotBlank(message = "Theater name is required")
        String name,

        @NotBlank(message = "Theater address is required")
        String address
) {
}
