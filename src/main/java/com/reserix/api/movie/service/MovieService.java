package com.reserix.api.movie.service;

import com.reserix.api.file.FileStorageService;
import com.reserix.api.file.ThumbnailUploadHelper;
import com.reserix.api.movie.dto.MovieCreateRequest;
import com.reserix.api.movie.dto.MovieResponse;
import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.entity.MovieThumbnail;
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
    private final ThumbnailUploadHelper thumbnailUploadHelper;

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
