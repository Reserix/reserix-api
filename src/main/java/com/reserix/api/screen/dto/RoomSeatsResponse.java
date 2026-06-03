package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.SeatType;

import java.util.List;

public record RoomSeatsResponse(
    Long roomId,
    List<SeatLayoutResponse> seats
) {
    public record SeatLayoutResponse(
            Integer rowNumber,
            Integer columnNumber,
            SeatType seatType,
            Boolean active,
            Integer displayOrder,
            String rowLabel,
            String seatLabel,
            Integer seatNumber
    ){}
}
