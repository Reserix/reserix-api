package com.reserix.api.movie.dto;

import com.reserix.api.movie.entity.Movie;

import java.time.LocalDateTime;

public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }
}
