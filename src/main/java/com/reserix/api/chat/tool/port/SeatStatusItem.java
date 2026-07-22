package com.reserix.api.chat.tool.port;
import java.math.BigDecimal;
public record SeatStatusItem(Long seatId, String code, String status, String seatType, BigDecimal price) { }
