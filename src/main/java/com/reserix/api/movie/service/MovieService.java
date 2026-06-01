package com.reserix.api.movie.service;

import com.reserix.api.file.FileStorageService;
import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public MovieResponse createMovie(MovieCreateRequest request, MultipartFile thumbnail) {
        String thumbnailUrl = fileStorageService.upload(thumbnail);

        Movie movie = new Movie(
                request.title(),
                request.description(),
                request.durationMinutes(),
                thumbnailUrl,
                request.director()
        );

        Movie createdMovie = movieRepository.save(movie);

        return MovieResponse.from(createdMovie);
    }

    public MovieResponse getMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        return MovieResponse.from(movie);
    }

    public List<MovieResponse> getMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieResponse::from)
                .toList();
    }
}
