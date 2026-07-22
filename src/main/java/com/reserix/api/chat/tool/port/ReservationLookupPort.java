package com.reserix.api.chat.tool.port;
import java.util.List;
public interface ReservationLookupPort { List<ReservationItem> getMyReservations(Long userId); }
