package com.reserix.api.movie.service;

import com.reserix.api.common.response.PageResponse;
import com.reserix.api.file.FileStorageService;
import com.reserix.api.file.ThumbnailUploadHelper;
import com.reserix.api.movie.dto.MovieCastInfo;
import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.entity.MovieCast;
import com.reserix.api.movie.entity.MovieThumbnail;
import com.reserix.api.movie.repository.MovieCastRepository;
import com.reserix.api.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final ThumbnailUploadHelper thumbnailUploadHelper;
    private final MovieCastRepository movieCastRepository;

    @Transactional
    public MovieResponse createMovie(MovieCreateRequest request, List<MultipartFile> thumbnails) {
        Movie movie = new Movie(
                request.title(),
                request.description(),
                request.durationMinutes(),
                request.director(),
                request.genre(),
                request.language(),
                request.minimumAge(),
                request.releaseDate(),
                request.trailerUrl()
        );

        thumbnailUploadHelper.addThumbnails(
                thumbnails,
                movie::addThumbnail
        );

        Movie createdMovie = movieRepository.save(movie);

        List<MovieCast> movieCasts = new ArrayList<>();
        for (MovieCastInfo cast : request.movieCasts()) {
            MovieCast movieCast = new MovieCast(
                    movie,
                    cast.actorName(),
                    cast.roleName()
            );
            movieCasts.add(movieCast);
        }

        movieCastRepository.saveAll(movieCasts);

        return MovieResponse.from(createdMovie);
    }

    public MovieResponse getMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        return MovieResponse.from(movie);
    }

    public PageResponse<MovieResponse> getMovies(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<MovieResponse> content = moviePage.getContent()
                .stream()
                .map(MovieResponse::from)
                .toList();

        return new PageResponse<>(
                content,
                moviePage.getNumber(),
                moviePage.getSize(),
                moviePage.getTotalElements(),
                moviePage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public List<Movie> searchMovies(String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);

        return movieRepository.searchByKeyword(
                normalizedKeyword,
                PageRequest.of(0, 10)
        );
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }

        String value = keyword.trim();

        if (value.isBlank()) {
            return "";
        }

        /*
         * Chatbot users often send full natural-language questions:
         * "What movies today?"
         * "show me movies"
         *
         * For these generic phrases, searching the literal full sentence is wrong.
         * Return a broad movie list instead.
         */
        String lower = value.toLowerCase();

        if (lower.contains("movie")
                || lower.contains("movies")
                || lower.contains("film")) {
            return "";
        }

        return value;
    }
}
