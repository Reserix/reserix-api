package com.reserix.api.payment.dto;

import com.reserix.api.movie.entity.Movie;
import com.reserix.api.payment.entity.Payment;

import java.math.BigDecimal;

public record MockPaymentIntentResponse(
        Long paymentId,
        Long reservationId,
        BigDecimal amount,
        String mockClientSecret
) {
    public static MockPaymentIntentResponse from(Payment payment) {
        return new MockPaymentIntentResponse(
            payment.getId(),
            payment.getReservation().getId(),
            payment.getAmount(),
                "mock_client_secret_" + payment.getId()
        );
    }
}
