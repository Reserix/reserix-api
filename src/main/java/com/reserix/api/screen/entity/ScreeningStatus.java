package com.reserix.api.screen.entity;

public enum ScreeningStatus {
    SCHEDULED,  // scheduled and available
    CANCELED,   // canceled by admin/theater
    CLOSED      // finished or no longer bookable
}
