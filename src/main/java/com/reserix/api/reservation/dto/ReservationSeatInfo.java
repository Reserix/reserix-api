package com.reserix.api.reservation.dto;

import com.reserix.api.screen.entity.SeatType;

public record ReservationSeatInfo(
        Long seatId,
        Integer rowNumber,
        Integer columnNumber,
        SeatType seatType,
        Integer price
) {
}
