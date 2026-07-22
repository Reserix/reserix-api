package com.reserix.api.chat.tool.port;
import java.time.LocalDateTime;
public record ScreeningSearchItem(Long screeningId, String movieTitle, String theaterName, String roomName, LocalDateTime startTime, LocalDateTime endTime, Integer availableSeatCount) { }
