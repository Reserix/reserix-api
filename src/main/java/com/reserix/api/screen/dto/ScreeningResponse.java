package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.entity.ScreeningPrice;
import com.reserix.api.screen.entity.ScreeningStatus;
import com.reserix.api.screen.entity.SeatType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ScreeningResponse(
        Long id,

        Long movieId,
        String movieTitle,

        Long roomId,
        String roomName,

        Long theaterId,
        String theaterName,
        String theaterAddress,

        LocalDateTime startTime,
        LocalDateTime endTime,

        ScreeningStatus status,

        Map<SeatType, Integer> seatPrices,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScreeningResponse from(Screening screening) {
        Map<SeatType, Integer> seatPrices = screening.getScreeningPrices()
                .stream()
                .collect(Collectors.toMap(
                        ScreeningPrice::getSeatType,
                        ScreeningPrice::getPrice
                ));

        return new ScreeningResponse(
                screening.getId(),

                screening.getMovie().getId(),
                screening.getMovie().getTitle(),

                screening.getRoom().getId(),
                screening.getRoom().getName(),

                screening.getRoom().getTheater().getId(),
                screening.getRoom().getTheater().getName(),
                screening.getRoom().getTheater().getAddress(),

                screening.getStartTime(),
                screening.getEndTime(),

                screening.getStatus(),

                seatPrices,

                screening.getCreatedAt(),
                screening.getUpdatedAt()
        );
    }
}
