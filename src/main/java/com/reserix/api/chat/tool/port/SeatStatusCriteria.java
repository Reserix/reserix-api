package com.reserix.api.chat.tool.port;
import java.util.List;
public record SeatStatusCriteria(Long screeningId, List<String> seatCodes) { }
