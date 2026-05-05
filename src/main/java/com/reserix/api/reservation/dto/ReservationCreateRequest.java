package com.reserix.api.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationCreateRequest(
        @NotNull(message = "screeningId is required")
        Long screeningId,

        @NotEmpty(message = "seatIds must not be empty")
        List<Long> seatIds
) {
}
