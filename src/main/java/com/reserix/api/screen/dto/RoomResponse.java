package com.reserix.api.screen.dto;

import com.reserix.api.screen.entity.Room;

import java.time.LocalDateTime;

public record RoomResponse(
        Long id,
        Long theaterId,
        String theaterName,
        String name,
        Integer rowCount,
        Integer columnCount,
        Long seatCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RoomResponse from(Room room, Long seatCount) {
        return new RoomResponse(
                room.getId(),
                room.getTheater().getId(),
                room.getTheater().getName(),
                room.getName(),
                room.getRowCount(),
                room.getColumnCount(),
                seatCount,
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
