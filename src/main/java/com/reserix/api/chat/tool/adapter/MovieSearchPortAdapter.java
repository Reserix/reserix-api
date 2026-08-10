package com.reserix.api.chat.tool.adapter;


import com.reserix.api.chat.tool.port.MovieSearchCriteria;
import com.reserix.api.chat.tool.port.MovieSearchItem;
import com.reserix.api.chat.tool.port.MovieSearchPort;
import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.service.MovieService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieSearchPortAdapter implements MovieSearchPort {
    private final MovieService movieService;

    public MovieSearchPortAdapter(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public List<MovieSearchItem> searchMovies(MovieSearchCriteria criteria) {
        String keyword = criteria.keyword();

        List<Movie> movies = movieService.searchMovies(keyword);

        return movies.stream()
                .map(movie -> new MovieSearchItem(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDurationMinutes(),
                        movie.getDescription()
                ))
                .toList();
    }
}
