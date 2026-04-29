package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.entity.ScreeningStatus;

import java.time.LocalDateTime;

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

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScreeningResponse from(Screening screening) {
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

                screening.getCreatedAt(),
                screening.getUpdatedAt()
        );
    }
}
