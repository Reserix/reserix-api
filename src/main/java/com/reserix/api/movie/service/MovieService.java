package com.reserix.api.movie.service;

import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;

    @Transactional
    public MovieResponse createMovie(MovieCreateRequest request) {
        Movie movie = new Movie(
                request.title(),
                request.description(),
                request.durationMinutes()
        );

        Movie createdMovie = movieRepository.save(movie);

        return MovieResponse.from(createdMovie);
    }

    public MovieResponse getMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        return MovieResponse.from(movie);
    }

    public List<MovieResponse> getMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieResponse::from)
                .toList();
    }
}
