package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.SeatType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.aspectj.weaver.ast.Not;

import java.time.LocalDateTime;
import java.util.Map;

public record ScreeningCreateRequest(
        @NotNull(message = "movieId is required")
        Long movieId,

        @NotNull(message = "roomId is required")
        Long roomId,

        @NotNull(message = "startTime is required")
        @Future
        LocalDateTime startTime,

        @NotNull(message = "seatPrices is required")
        Map<SeatType, Integer> seatPrices
) {
}
