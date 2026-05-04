package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.SeatType;

public record SeatStatusResponse(
        Long seatId,
        Integer rowNumber,
        Integer columnNumber,
        SeatType seatType,
        Boolean status
) {
}
