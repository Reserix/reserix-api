package com.reserix.api.theater.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.common.response.PageResponse;
import com.reserix.api.theater.dto.TheaterCreateRequest;
import com.reserix.api.theater.dto.TheaterResponse;
import com.reserix.api.theater.service.TheaterService;
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
@RequestMapping("/api/v1/theaters")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;
    private final ObjectMapper objectMapper;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<TheaterResponse>> createTheater(
            @Valid @RequestPart("request") String requestJson,
            @RequestPart(value = "thumbnails", required = false) List<MultipartFile> thumbnails
    ) {
        TheaterCreateRequest request = objectMapper.readValue(requestJson, TheaterCreateRequest.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Theater created",
                        theaterService.createTheater(request, thumbnails)
                ));
    }

    @GetMapping("/{theaterId}")
    public ResponseEntity<ApiResponse<TheaterResponse>> getTheater(
            @PathVariable Long theaterId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(theaterService.getTheater(theaterId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TheaterResponse>>> getTheaters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(theaterService.getTheaters(page, size)));
    }
}
