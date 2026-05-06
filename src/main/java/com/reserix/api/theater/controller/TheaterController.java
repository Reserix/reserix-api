package com.reserix.api.theater.controller;

import com.reserix.api.theater.dto.TheaterCreateRequest;
import com.reserix.api.theater.dto.TheaterResponse;
import com.reserix.api.theater.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theaters")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @PostMapping
    public TheaterResponse createTheater(
            @Valid @RequestBody TheaterCreateRequest request
    ) {
            return theaterService.createTheater(request);
    }

    @GetMapping("/{theaterId}")
    public TheaterResponse getTheater(
            @PathVariable Long theaterId
    ) {
        return theaterService.getTheater(theaterId);
    }

    @GetMapping
    public List<TheaterResponse> getTheaters() {
        return theaterService.getTheaters();
    }
}
