package com.reserix.api.reservation.websocket;

import com.reserix.api.reservation.entity.ReservationSeatStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SeatStatusEvent(
        Long screeningId,
        List<Long> seatIds,
        String seatCode,
        ReservationSeatStatus type,
        Long userId,
        LocalDateTime occurredAt
) {
}