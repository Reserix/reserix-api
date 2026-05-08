package com.reserix.api.screen.service;

import com.reserix.api.common.exception.BusinessException;
import com.reserix.api.common.exception.ErrorCode;
import com.reserix.api.movie.entity.Movie;
import com.reserix.api.movie.repository.MovieRepository;
import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.repository.ReservationRepository;
import com.reserix.api.reservation.repository.ReservationSeatRepository;
import com.reserix.api.screen.dto.ScreeningCreateRequest;
import com.reserix.api.screen.dto.ScreeningResponse;
import com.reserix.api.screen.dto.ScreeningSeatResponse;
import com.reserix.api.screen.entity.*;
import com.reserix.api.screen.repository.RoomRepository;
import com.reserix.api.screen.repository.ScreeningPriceRepository;
import com.reserix.api.screen.repository.ScreeningRepository;
import com.reserix.api.screen.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ScreeningPriceRepository screeningPriceRepository;

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

        List<ScreeningPrice> screeningPrices = new ArrayList<>();

        for (SeatType seatType : request.seatPrices().keySet()) {
            ScreeningPrice screeningPrice = new ScreeningPrice(
                    savedScreening,
                    seatType,
                    request.seatPrices().get(seatType)
                    );
            screeningPrices.add(screeningPrice);
        }

        screeningPriceRepository.saveAll(screeningPrices);

        Screening result = screeningRepository.findById(savedScreening.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CONFLICT, "Screening not found"));

        return ScreeningResponse.from(result);
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

    public ScreeningSeatResponse getSeatStatus(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        List<Seat> seats = seatRepository.findSeatsByRoomId(screening.getRoom().getId());
        List<ReservationSeat> reservationSeats = reservationSeatRepository.findReservationSeatsByScreening(screening);

        return ScreeningSeatResponse.from(screening, seats, reservationSeats);
    }
}
