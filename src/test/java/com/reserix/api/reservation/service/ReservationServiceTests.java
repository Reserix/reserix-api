package com.reserix.api.reservation.service;


import com.reserix.api.movie.entity.Movie;
import com.reserix.api.reservation.dto.ReservationCreateRequest;
import com.reserix.api.reservation.dto.ReservationResponse;
import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.entity.ReservationSeatStatus;
import com.reserix.api.reservation.entity.ReservationStatus;
import com.reserix.api.reservation.repository.ReservationRepository;
import com.reserix.api.reservation.repository.ReservationSeatRepository;
import com.reserix.api.screen.entity.*;
import com.reserix.api.screen.repository.ScreeningRepository;
import com.reserix.api.screen.repository.SeatRepository;
import com.reserix.api.theater.entity.Theater;
import com.reserix.api.user.entity.User;
import com.reserix.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private SeatLockService seatLockService;

    private User user;
    private Screening screening;
    private ScreeningPrice screeningPriceSTD;
    private ScreeningPrice screeningPriceVIP;
    private ScreeningPrice screeningPricePRE;
    private Seat seat1;
    private Seat seat2;

    private Theater theater;
    private Room room;
    private Movie movie;

    @BeforeEach
    void setUp() {
        theater = new Theater("AMC Fresh Meadows 7", "190-02 Horace Harding Expy, Fresh Meadows, NY 11365");
        room = new Room(theater, "R201", 20, 50);
        movie = new Movie("Joker", "A mentally troubled comedian descends into madness.", 122);
        screening = new Screening(movie, room, LocalDateTime.parse("2026-05-15T00:00:00"), LocalDateTime.parse("2026-05-15T02:00:00"));
        screeningPriceSTD = new ScreeningPrice(screening, SeatType.STANDARD, 10000);
        screeningPriceVIP = new ScreeningPrice(screening, SeatType.VIP, 15000);
        screeningPricePRE = new ScreeningPrice(screening, SeatType.PREMIUM, 20000);

        user = mock(User.class);
        seat1 = mock(Seat.class);
        seat2 = mock(Seat.class);

        ReflectionTestUtils.setField(theater, "id", 1L);
        ReflectionTestUtils.setField(room, "id", 1L);
        ReflectionTestUtils.setField(room, "theater", theater);
        ReflectionTestUtils.setField(movie, "id", 1L);
        ReflectionTestUtils.setField(screening, "id", 10L);
        ReflectionTestUtils.setField(screeningPriceSTD, "id", 1L);
        ReflectionTestUtils.setField(screeningPriceSTD, "screening", screening);
        ReflectionTestUtils.setField(screeningPriceVIP, "id", 2L);
        ReflectionTestUtils.setField(screeningPriceVIP, "screening", screening);
        ReflectionTestUtils.setField(screeningPricePRE, "id", 3L);
        ReflectionTestUtils.setField(screeningPricePRE, "screening", screening);

        ReflectionTestUtils.setField(screening, "screeningPrices", List.of(screeningPriceSTD, screeningPriceVIP, screeningPricePRE));
    }

    @Test
    void createReservation_success_shouldCreatePendingReservation() {
        // given
        when(user.getId()).thenReturn(1L);
        // when(seat1.getId()).thenReturn(100L);
        // when(seat2.getId()).thenReturn(101L);
        when(seat1.getRoom()).thenReturn(room);
        when(seat2.getRoom()).thenReturn(room);
        when(seat1.getSeatType()).thenReturn(SeatType.STANDARD);
        when(seat2.getSeatType()).thenReturn(SeatType.STANDARD);

        ReservationCreateRequest request = new ReservationCreateRequest(
                10L,
                List.of(100L, 101L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));
        when(seatRepository.findAllById(List.of(100L, 101L))).thenReturn(List.of(seat1, seat2));

//        doReturn(false).when(reservationSeatRepository)
//                .existsByScreeningIdAndSeatIdAndStatusIn(
//                        eq(10L),
//                        anyLong(),
//                        eq(List.of(
//                                ReservationSeatStatus.PENDING,
//                                ReservationSeatStatus.CONFIRMED
//                        ))
//                );

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(reservationSeatRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ReservationResponse response = reservationService.createReservation(request, 1L);

        // then
        ArgumentCaptor<Reservation> reservationCaptor =
                ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository).save(reservationCaptor.capture());

        Reservation savedReservation = reservationCaptor.getValue();

        assertThat(savedReservation.getUser()).isEqualTo(user);
        assertThat(savedReservation.getScreening()).isEqualTo(screening);
        assertThat(savedReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(savedReservation.getExpireAt()).isAfter(LocalDateTime.now());

        ArgumentCaptor<List<ReservationSeat>> reservationSeatsCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(reservationSeatRepository).saveAll(reservationSeatsCaptor.capture());

        List<ReservationSeat> savedSeats = reservationSeatsCaptor.getValue();

        assertThat(savedSeats).hasSize(2);
        assertThat(savedSeats)
                .allMatch(reservationSeat ->
                        reservationSeat.getStatus() == ReservationSeatStatus.PENDING
                );

        assertThat(response).isNotNull();
    }

    @Test
    void createReservation_fail_whenUserNotFound() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L,
                List.of(100L)
        );

        // when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(request, 2L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reservationRepository, never()).save(any());
        verify(reservationSeatRepository, never()).saveAll(anyList());
    }

    @Test
    void createReservation_fail_whenScreeningNotFound() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L,
                List.of(100L)
        );

        // when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(screeningRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(request, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reservationRepository, never()).save(any());
        verify(reservationSeatRepository, never()).saveAll(anyList());
    }

    @Test
    void createReservation_fail_whenSomeSeatNotFound() {
        // given
        ReservationCreateRequest request = new ReservationCreateRequest(
                10L,
                List.of(100L, 101L)
        );

        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));

        Seat seat = new Seat(room, 0, 0);
        // only one seat returned
        when(seatRepository.findAllById(List.of(100L, 101L))).thenReturn(List.of(seat));

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(request, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reservationRepository, never()).save(any());
        verify(reservationSeatRepository, never()).saveAll(anyList());
    }

//    @Test
//    void createReservation_fail_whenSeatAlreadyPending() {
//        // given
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                1L,
//                List.of(100L)
//        );
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));
//        when(seatRepository.findAllById(List.of(100L))).thenReturn(List.of(seat1));
//
//        when(reservationSeatRepository.existsByScreeningIdAndSeatIdAndStatusIn(
//                eq(10L),
//                eq(100L),
//                eq(List.of(ReservationSeatStatus.PENDING, ReservationSeatStatus.CONFIRMED))
//        )).thenReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> reservationService.createReservation(request))
//                .isInstanceOf(IllegalArgumentException.class);
//
//        verify(reservationRepository, never()).save(any());
//        verify(reservationSeatRepository, never()).saveAll(anyList());
//    }
//
//    @Test
//    void createReservation_fail_whenSeatAlreadyConfirmed() {
//        // given
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                1L,
//                List.of(100L)
//        );
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));
//        when(seatRepository.findAllById(List.of(100L))).thenReturn(List.of(seat1));
//
//        when(reservationSeatRepository.existsByScreeningIdAndSeatIdAndStatusIn(
//                eq(10L),
//                eq(100L),
//                eq(List.of(ReservationSeatStatus.PENDING, ReservationSeatStatus.CONFIRMED))
//        )).thenReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> reservationService.createReservation(request))
//                .isInstanceOf(IllegalArgumentException.class);
//
//        verify(reservationRepository, never()).save(any());
//        verify(reservationSeatRepository, never()).saveAll(anyList());
//    }
//
//    @Test
//    void createReservation_success_shouldSetExpireAtAboutFiveMinutesLater() {
//        // given
//        ReservationCreateRequest request = new ReservationCreateRequest(
//                1L,
//                10L,
//                List.of(100L)
//        );
//
//        LocalDateTime before = LocalDateTime.now();
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(screeningRepository.findById(10L)).thenReturn(Optional.of(screening));
//        when(seatRepository.findAllById(List.of(100L))).thenReturn(List.of(seat1));
//
//        when(reservationSeatRepository.existsByScreeningIdAndSeatIdAndStatusIn(
//                eq(10L),
//                eq(100L),
//                eq(List.of(ReservationSeatStatus.PENDING, ReservationSeatStatus.CONFIRMED))
//        )).thenReturn(false);
//
//        when(reservationRepository.save(any(Reservation.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        when(reservationSeatRepository.saveAll(anyList()))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        reservationService.createReservation(request);
//
//        // then
//        ArgumentCaptor<Reservation> reservationCaptor =
//                ArgumentCaptor.forClass(Reservation.class);
//
//        verify(reservationRepository).save(reservationCaptor.capture());
//
//        Reservation savedReservation = reservationCaptor.getValue();
//
//        assertThat(savedReservation.getExpireAt()).isNotNull();
//        assertThat(savedReservation.getExpireAt()).isAfter(before.plusMinutes(4));
//        assertThat(savedReservation.getExpireAt()).isBefore(before.plusMinutes(6));
//    }
}
