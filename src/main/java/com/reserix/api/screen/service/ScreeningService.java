package com.reserix.api.screen.service;

import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.repository.MovieRepository;
import com.reserix.api.screen.dto.ScreeningCreateRequest;
import com.reserix.api.screen.dto.ScreeningResponse;
import com.reserix.api.screen.entity.Room;
import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.repository.RoomRepository;
import com.reserix.api.screen.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;

    @Transactional(rollbackFor = Exception.class)
    public ScreeningResponse createScreen(ScreeningCreateRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        if (screeningRepository.existsByRoomIdAndStartTime(request.roomId(), request.startTime())) {
            throw new IllegalArgumentException("Room already exists at this theater");
        }

        Screening screening = new Screening(
                movie,
                room,
                request.startTime(),
                request.startTime().plusMinutes(movie.getDurationMinutes())
        );

        Screening savedScreening = screeningRepository.save(screening);
        return ScreeningResponse.from(savedScreening);
    }

    public ScreeningResponse getScreen(Long screenId) {
        Screening screening = screeningRepository.findById(screenId)
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        return ScreeningResponse.from(screening);
    }

    public List<ScreeningResponse> getAll() {
        return screeningRepository.findAll()
                .stream()
                .map(ScreeningResponse::from)
                .toList();
    }
}
