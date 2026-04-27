package com.reserix.api.payment.entity;

public enum PaymentStatus {
    PENDING,    // Payment requested
    SUCCESS,    // Payment succeed
    FAILED,     // Payment failed
    CANCELED    // Payment canceled
}
