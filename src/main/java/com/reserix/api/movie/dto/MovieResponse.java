package com.reserix.api.movie.dto;

import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.entity.MovieThumbnail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String director,
        BigDecimal averageRating,
        Integer ratingCount,
        String genre,
        String language,
        Integer minimumAge,
        LocalDate releaseDate,
        String trailerUrl,
        List<String> thumbnailUrls,
        List<MovieCastInfo> movieCasts,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getDirector(),
                movie.getAverageRating(),
                movie.getRatingCount(),
                movie.getGenre(),
                movie.getLanguage(),
                movie.getMinimumAge(),
                movie.getReleaseDate(),
                movie.getTrailerUrl(),
                movie.getThumbnails()
                        .stream()
                        .map(MovieThumbnail::getFilePath)
                        .toList(),
                movie.getCasts()
                        .stream()
                        .map(cast -> new MovieCastInfo(
                                cast.getActorName(),
                                cast.getRoleName()
                        ))
                        .toList(),
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }
}
