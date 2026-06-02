package com.reserix.api.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record MovieCreateRequest(
        @NotBlank(message = "Movie title is required")
        String title,

        @NotBlank(message = "Movie description is required")
        String description,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be greater than 0")
        @Max(value = 1000, message = "Duration must be less than 1000")
        Integer durationMinutes,

        @NotBlank(message = "Director is required")
        String director,

        @NotBlank(message = "Genre is required")
        String genre,

        @NotBlank(message = "Language is required")
        String language,

        @NotNull(message = "Minimum age is required")
        @Min(0)
        Integer minimumAge,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate,

        String trailerUrl,

        List<MovieCastInfo> movieCasts
) {
}
