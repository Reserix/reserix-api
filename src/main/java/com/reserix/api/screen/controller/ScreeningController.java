package com.reserix.api.screen.controller;

import com.reserix.api.screen.dto.ScreeningCreateRequest;
import com.reserix.api.screen.dto.ScreeningResponse;
import com.reserix.api.screen.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService screeningService;

    @PostMapping
    public ScreeningResponse createScreening(
            @Valid @RequestBody ScreeningCreateRequest request
    ) {
        return screeningService.createScreen(request);
    }

    @GetMapping("{screeningId}")
    public ScreeningResponse getScreening(
            @PathVariable Long screeningId
    ) {
        return screeningService.getScreen(screeningId);
    }

    @GetMapping
    public List<ScreeningResponse> getAll() {
        return screeningService.getAll();
    }
}
