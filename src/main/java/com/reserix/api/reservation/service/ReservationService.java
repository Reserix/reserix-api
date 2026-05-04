package com.reserix.api.reservation.service;

import com.reserix.api.reservation.dto.ReservationCreateRequest;
import com.reserix.api.reservation.dto.ReservationResponse;
import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.repository.ReservationRepository;
import com.reserix.api.reservation.repository.ReservationSeatRepository;
import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.entity.Seat;
import com.reserix.api.screen.repository.ScreeningRepository;
import com.reserix.api.screen.repository.SeatRepository;
import com.reserix.api.user.entity.User;
import com.reserix.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final SeatRepository seatRepository;
    private final ScreeningRepository screeningRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final UserRepository userRepository;

    private final Integer SEAT_LOCK_TIME = 5;

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @Transactional(rollbackFor = Exception.class)
    public ReservationResponse createReservation(ReservationCreateRequest request, Long userId) {
        Screening screening = screeningRepository.findById(request.screenId())
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        List<Long> seatIds = request.seatIds();

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("seatIds must not be empty");
        }

        // 1. Check if there is a double seats
        if (seatIds.size() != new HashSet<>(seatIds).size()) {
            throw new IllegalArgumentException("Duplicate seatIds are not allowed");
        }

        // 2. Check if the seats exists in the room
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("Some seats do not exist");
        }

        boolean hasInvalidSeat = seats.stream()
                .anyMatch(seat -> !seat.getRoom().getId().equals(screening.getRoom().getId()));

        if (hasInvalidSeat) {
            throw new IllegalArgumentException("Some seats do not belong to this screening room");
        }

        // 3. Check if the seats are reserved or not.
        List<Long> reservedSeatIds = reservationSeatRepository.findReservedSeatIds(request.screenId(),seatIds);

        if (!reservedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Already reserved seats: " + reservedSeatIds);
        }

        // 4. Create reservation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not exists"));
        try {
            Reservation reservation = new Reservation(
                    user,
                    screening,
                    LocalDateTime.now().plusMinutes(SEAT_LOCK_TIME)
            );

            Reservation savedReservation = reservationRepository.save(reservation);

            List<ReservationSeat> reservationSeats = new ArrayList<>();
            for (Seat seat : seats) {
                ReservationSeat reservationSeat = new ReservationSeat(
                        savedReservation,
                        seat
                );

                reservationSeats.add(reservationSeat);
            }

            List<ReservationSeat> savedReservationSeats = reservationSeatRepository.saveAll(reservationSeats);

            return ReservationResponse.from(reservation, savedReservationSeats);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("The seats are already reserved");
        }
    }
}
