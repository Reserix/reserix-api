package com.reserix.api.chat.tool.port;
public interface PaymentStatusPort {
    PaymentStatusInfo getPaymentStatus(Long userId, Long reservationId);
}
