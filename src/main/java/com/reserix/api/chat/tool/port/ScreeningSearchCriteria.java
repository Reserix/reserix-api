package com.reserix.api.chat.tool.port;
import java.time.LocalDate;
public record ScreeningSearchCriteria(
        Long movieId,
        String movieTitle,
        LocalDate date,
        Long theaterId,
        String timeRange) {

}
