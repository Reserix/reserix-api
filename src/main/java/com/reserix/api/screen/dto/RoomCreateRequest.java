package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.SeatType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

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
        Integer columnCount,

        @Valid
        @NotNull(message = "seats is required")
        List<SeatLayoutRequest> seats
) {
        public record SeatLayoutRequest(
                @NotNull(message = "rowNumber is required")
                @Min(value = 0, message = "rowNumber must be at least 0")
                Integer rowNumber,

                @NotNull(message = "columnNumber is required")
                @Min(value = 0, message = "columnNumber must be at least 0")
                Integer columnNumber,

                @NotBlank(message = "rowLabel is required")
                String rowLabel,

                @NotNull(message = "seatNumber is required")
                @Min(value = 1, message = "seatNumber must be at least 1")
                Integer seatNumber,

                SeatType seatType,

                Boolean active
        ) {
        }
}
