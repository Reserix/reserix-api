package com.reserix.api.theater.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.theater.dto.TheaterCreateRequest;
import com.reserix.api.theater.dto.TheaterResponse;
import com.reserix.api.theater.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theaters")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @PostMapping
    public ResponseEntity<ApiResponse<TheaterResponse>> createTheater(
            @Valid @RequestBody TheaterCreateRequest request
    ) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Theater created", theaterService.createTheater(request)));
    }

    @GetMapping("/{theaterId}")
    public ResponseEntity<ApiResponse<TheaterResponse>> getTheater(
            @PathVariable Long theaterId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(theaterService.getTheater(theaterId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterResponse>>> getTheaters() {
        return ResponseEntity
                .ok(ApiResponse.success(theaterService.getTheaters()));
    }
}
