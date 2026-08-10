package com.reserix.api.chat.tool.port;
import java.util.List;

public interface MovieSearchPort {
    List<MovieSearchItem> searchMovies(MovieSearchCriteria criteria);
}
