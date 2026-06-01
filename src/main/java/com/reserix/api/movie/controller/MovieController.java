package com.reserix.api.movie.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.common.response.PageResponse;
import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final ObjectMapper objectMapper;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(
            @Valid @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnails", required = false) List<MultipartFile> thumbnails
    ) {
        MovieCreateRequest request = objectMapper.readValue(requestJson, MovieCreateRequest.class);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Movie created",
                                movieService.createMovie(request, thumbnails)
                        )
                );
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieByID(
            @PathVariable Long movieId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(movieService.getMovie(movieId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MovieResponse>>> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(movieService.getMovies(page, size)));
    }
}
