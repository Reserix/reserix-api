package com.reserix.api.movie.controller;

import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public MovieResponse createMovie(
            @Valid @RequestBody MovieCreateRequest request
    ) {
        return movieService.createMovie(request);
    }

    @GetMapping("/{movieId}")
    public MovieResponse getMovieByID(
            @PathVariable Long movieId
    ) {
        return movieService.getMovie(movieId);
    }

    @GetMapping
    public List<MovieResponse> getMovies() {
        return movieService.getMovies();
    }
}
