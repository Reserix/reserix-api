package com.reserix.api.theater.dto;

import com.reserix.api.theater.entity.Theater;

import java.time.LocalDateTime;

public record TheaterResponse(
        Long id,
        String name,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TheaterResponse from(Theater theater) {
        return new TheaterResponse(
                theater.getId(),
                theater.getName(),
                theater.getAddress(),
                theater.getCreatedAt(),
                theater.getUpdatedAt()
        );
    }
}
