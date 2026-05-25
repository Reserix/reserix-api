package com.reserix.api.reservation.service;

import com.reserix.api.common.exception.BusinessException;
import com.reserix.api.common.exception.ErrorCode;
import com.reserix.api.reservation.dto.ReservationCreateRequest;
import com.reserix.api.reservation.dto.ReservationResponse;
import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.entity.ReservationSeatStatus;
import com.reserix.api.reservation.entity.ReservationStatus;
import com.reserix.api.reservation.repository.ReservationRepository;
import com.reserix.api.reservation.repository.ReservationSeatRepository;
import com.reserix.api.screen.dto.RoomResponse;
import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.entity.ScreeningPrice;
import com.reserix.api.screen.entity.Seat;
import com.reserix.api.screen.entity.SeatType;
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
import java.util.Map;
import java.util.stream.Collectors;

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
    private final SeatLockService seatLockService;

    @Transactional(rollbackFor = Exception.class)
    public ReservationResponse createReservation(ReservationCreateRequest request, Long userId) {
        Screening screening = screeningRepository.findById(request.screeningId())
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
        List<Long> unavailableSeatIds = reservationSeatRepository.findUnavailableSeatIds(
                request.screeningId(),
                seatIds,
                List.of(
                        ReservationSeatStatus.PENDING,
                        ReservationSeatStatus.CONFIRMED
                )
        );

        if (!unavailableSeatIds.isEmpty()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Some seats are already reserved: " + unavailableSeatIds);
        }

        // 4. Create reservation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not exists"));

        seatLockService.lockSeats(request.screeningId(), seatIds, userId);

        try {
            Reservation reservation = new Reservation(
                    user,
                    screening,
                    LocalDateTime.now().plusMinutes(SEAT_LOCK_TIME)
            );

            Reservation savedReservation = reservationRepository.save(reservation);

            // Get prices for screening seat
            Map<SeatType, Integer> seatPrices = screening.getScreeningPrices()
                    .stream()
                    .collect(Collectors.toMap(
                            ScreeningPrice::getSeatType,
                            ScreeningPrice::getPrice
                    ));

            List<ReservationSeat> reservationSeats = new ArrayList<>();
            for (Seat seat : seats) {
                // get price for seat
                Integer seatPrice = seatPrices.get(seat.getSeatType());

                ReservationSeat reservationSeat = new ReservationSeat(
                        savedReservation,
                        seat,
                        seatPrice
                );

                reservationSeats.add(reservationSeat);
            }

            List<ReservationSeat> savedReservationSeats = reservationSeatRepository.saveAll(reservationSeats);

            return ReservationResponse.from(reservation, savedReservationSeats);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("The seats are already reserved");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ReservationResponse cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Reservation not found"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "You cannot cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalArgumentException("Reservation already canceled");
        }

        if (reservation.getStatus() == ReservationStatus.EXPIRED) {
            throw new IllegalArgumentException("Reservation already expired");
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalArgumentException("Confirmed reservation cannot be canceled yet");
        }

        reservation.cancel();

        List<ReservationSeat> seats =
                reservationSeatRepository.findByReservationId(reservationId);

        for (ReservationSeat seat : seats) {
            seat.release();
            seatLockService.unlockSeat(
                    reservation.getScreening().getId(),
                    seat.getSeat().getId(),
                    userId
            );
        }

        return ReservationResponse.from(reservation, seats);
    }

    public List<ReservationResponse> getMyReservation(Long userId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not exists"));

        return reservationRepository.findReservationByUser(me)
                .stream()
                .map(reservation -> {
                    List<ReservationSeat> seats =
                            reservationSeatRepository.findByReservationId(reservation.getId());
                    return ReservationResponse.from(reservation, seats);
                })
                .toList();
    }
}
