package com.reserix.api.chat.tool.port;
import java.util.List;
public interface ScreeningSearchPort {
    List<ScreeningSearchItem> searchScreenings(ScreeningSearchCriteria criteria);
}
