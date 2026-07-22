package com.reserix.api.chat.tool.port;
import java.util.List;
public interface SeatStatusPort { List<SeatStatusItem> getSeatStatus(SeatStatusCriteria criteria); }
