package com.reserix.api.movie.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(
            @Valid @RequestBody MovieCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movie created", movieService.createMovie(request)));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieByID(
            @PathVariable Long movieId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(movieService.getMovie(movieId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getMovies() {
        return ResponseEntity
                .ok(ApiResponse.success(movieService.getMovies()));
    }
}
