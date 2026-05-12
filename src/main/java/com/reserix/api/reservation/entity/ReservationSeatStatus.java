package com.reserix.api.reservation.entity;

public enum ReservationSeatStatus {
    AVAILABLE,    // Seat available
    PENDING,     // Seat was locked (PENDING)
    CONFIRMED,  // Confirmed (CONFIRMED)
    EXPIRED,    // Time expired
    CANCELLED,   // User canceled
    FAILED,          // Payment failed
    RELEASED,       // Seat released
}
