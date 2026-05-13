package com.reserix.api.payment.dto;

import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.entity.Movie;
import jakarta.validation.constraints.NotNull;

public record MockPaymentIntentRequest(
        @NotNull(message = "reservationId is required")
        Long reservationId
) {
}
