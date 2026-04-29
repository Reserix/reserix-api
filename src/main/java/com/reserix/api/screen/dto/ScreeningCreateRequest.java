package com.reserix.api.screen.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScreeningCreateRequest(
        @NotNull(message = "movieId is required")
        Long movieId,

        @NotNull(message = "roomId is required")
        Long roomId,

        @NotNull(message = "startTime is required")
        @Future
        LocalDateTime startTime
) {
}
