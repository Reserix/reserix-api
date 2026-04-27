package com.reserix.api.reservation.entity;

public enum ReservationStatus {
    PENDING,    // Seat locked and waiting user's payment
    CONFIRMED,  // Payment succeed and confirmed
    CANCELED,   // User cancel the payment
    EXPIRED     // Seat locking was expired
}
