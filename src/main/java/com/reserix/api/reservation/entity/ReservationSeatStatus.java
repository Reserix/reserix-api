package com.reserix.api.reservation.entity;

public enum ReservationSeatStatus {
    AVAILABLE,
    LOCKED,     // Seat was locked (PENDING)
    CONFIRMED,  // Confirmed (CONFIRMED)
    RELEASED    // Released for user canceling the payment or expired
}
