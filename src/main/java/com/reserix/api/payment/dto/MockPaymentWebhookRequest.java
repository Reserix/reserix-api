package com.reserix.api.payment.dto;

public record MockPaymentWebhookRequest(
        Long paymentId,
        MockPaymentResult result
) {
}
