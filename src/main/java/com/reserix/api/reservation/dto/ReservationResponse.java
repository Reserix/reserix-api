package com.reserix.api.reservation.dto;

import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.reservation.entity.ReservationSeat;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long reservationId,
        Long userId,
        String theaterName,
        String roomName,
        String movieName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<Long> seats
) {
    public static ReservationResponse from(Reservation reservation, List<ReservationSeat> seats) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getScreening().getRoom().getTheater().getName(),
                reservation.getScreening().getRoom().getName(),
                reservation.getScreening().getMovie().getTitle(),
                reservation.getScreening().getStartTime(),
                reservation.getScreening().getEndTime(),
                seats.stream().map((seat)-> seat.getSeat().getId()).toList()
        );
    }
}
