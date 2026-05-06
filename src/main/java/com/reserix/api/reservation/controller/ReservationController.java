package com.reserix.api.reservation.controller;

import com.reserix.api.reservation.dto.ReservationCreateRequest;
import com.reserix.api.reservation.dto.ReservationResponse;
import com.reserix.api.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ReservationResponse createReservation(
            @Valid @RequestBody ReservationCreateRequest request,
            @AuthenticationPrincipal Long userId)
    {
        return reservationService.createReservation(request, userId);
    }
}
