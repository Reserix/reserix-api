package com.reserix.api.reservation.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.reservation.dto.ReservationCreateRequest;
import com.reserix.api.reservation.dto.ReservationResponse;
import com.reserix.api.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Valid @RequestBody ReservationCreateRequest request,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Reservation created",
                                reservationService.createReservation(request, userId)
                        )
                );
    }

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal Long userId
    ) {
        ReservationResponse response =
                reservationService.cancelReservation(reservationId, userId);

        return ResponseEntity.ok(ApiResponse.success("Reservation canceled", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservation(
            @AuthenticationPrincipal Long userId
    ) {
        List<ReservationResponse> response =
                reservationService.getMyReservation(userId);

        return ResponseEntity.ok(ApiResponse.success("My reservations", response));
    }
}
