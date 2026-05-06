package com.reserix.api.screen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomCreateRequest(
        @NotNull(message = "theaterId is required")
        Long theaterId,

        @NotBlank(message = "Room name is required")
        String name,

        @NotNull(message = "rowCount is required")
        @Min(1)
        Integer rowCount,

        @NotNull(message = "columnCount is required")
        @Min(1)
        Integer columnCount
) {
}
