package com.reserix.api.screen.dto;

import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.entity.ReservationSeatStatus;
import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.entity.Seat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record ScreeningSeatResponse(
    Long screeningId,
    String theaterName,
    String roomName,
    String movieName,
    List<SeatStatusResponse> seatsStatus
) {
    public static ScreeningSeatResponse from(Screening screening, List<Seat> seats, List<ReservationSeat> reservationSeats) {
        Set<Long> reservedSeatIds = reservationSeats.stream()
                .map(reservationSeat -> reservationSeat.getSeat().getId())
                .collect(Collectors.toSet());

        Map<Long, ReservationSeatStatus> reservedSeatStatusMap = reservationSeats.stream()
                .collect(Collectors.toMap(
                        rs -> rs.getSeat().getId(),
                        ReservationSeat::getStatus
                ));

        List<SeatStatusResponse> seatsStatus = seats.stream()
                .map(seat -> new SeatStatusResponse(
                        seat.getId(),
                        seat.getRowNumber(),
                        seat.getColumnNumber(),
                        seat.getSeatType(),
                        reservedSeatIds.contains(seat.getId()) ? reservedSeatStatusMap.get(seat.getId()) : ReservationSeatStatus.AVAILABLE
                ))
                .toList();

        return new ScreeningSeatResponse(
                screening.getId(),
                screening.getRoom().getTheater().getName(),
                screening.getRoom().getName(),
                screening.getMovie().getTitle(),
                seatsStatus
        );
    }
}
