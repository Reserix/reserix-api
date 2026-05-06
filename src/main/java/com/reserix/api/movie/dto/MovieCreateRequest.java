package com.reserix.api.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovieCreateRequest(
        @NotBlank(message = "Movie title is required")
        String title,

        @NotBlank(message = "Movie description is required")
        String description,

        @NotNull
        @Min(0)
        @Max(1000)
        Integer durationMinutes
) {
}
