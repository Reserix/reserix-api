package com.reserix.api.chat.tool.port;
import java.time.LocalDateTime;
import java.util.List;
public record ReservationItem(Long reservationId, String movieTitle, LocalDateTime startTime, List<String> seats, String reservationStatus, LocalDateTime expireAt) { }
