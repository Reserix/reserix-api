package com.reserix.api.screen.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.common.response.PageResponse;
import com.reserix.api.screen.dto.ScreeningCreateRequest;
import com.reserix.api.screen.dto.ScreeningResponse;
import com.reserix.api.screen.dto.ScreeningSeatResponse;
import com.reserix.api.screen.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService screeningService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScreeningResponse>> createScreening(
            @Valid @RequestBody ScreeningCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Screening created", screeningService.createScreen(request)));
    }

    @GetMapping("{screeningId}")
    public ResponseEntity<ApiResponse<ScreeningResponse>> getScreening(
            @PathVariable Long screeningId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(screeningService.getScreen(screeningId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ScreeningResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(screeningService.getAll(page, size)));
    }

    @GetMapping("{screeningId}/seats")
    public ResponseEntity<ApiResponse<ScreeningSeatResponse>> getSeatStatus(
            @PathVariable Long screeningId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(screeningService.getSeatStatus(screeningId)));
    }
}
