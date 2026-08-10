package com.reserix.api.chat.tool.port;
import java.math.BigDecimal;
public record PaymentStatusInfo(
        Long reservationId,
        String reservationStatus,
        String paymentStatus,
        BigDecimal amount) {

}
