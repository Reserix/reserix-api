package com.reserix.api.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationCreateRequest(
        @NotNull(message = "screenId is required")
        Long screenId,

        @NotEmpty(message = "seatIds must not be empty")
        List<Long> seatIds
) {
}
