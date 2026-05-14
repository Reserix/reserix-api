package com.reserix.api.reservation.service;

import com.reserix.api.reservation.entity.ReservationSeatStatus;
import com.reserix.api.reservation.entity.ReservationStatus;
import com.reserix.api.reservation.repository.ReservationRepository;
import com.reserix.api.reservation.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationExpirationService {
    private static final int BATCH_SIZE = 500;

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Transactional
    public int expirePendingReservations() {
        LocalDateTime now = LocalDateTime.now();

        List<Long> expiredReservationIds =
                reservationRepository.findExpiredPendingReservationIds(
                        ReservationStatus.PENDING,
                        now,
                        PageRequest.of(0, BATCH_SIZE)
                );

        if (expiredReservationIds.isEmpty()) {
            return 0;
        }

        reservationSeatRepository.bulkReleaseReservationSeats(
                expiredReservationIds,
                ReservationSeatStatus.PENDING,
                ReservationSeatStatus.RELEASED
        );

        return reservationRepository.bulkExpireReservations(
                expiredReservationIds,
                ReservationStatus.PENDING,
                ReservationStatus.EXPIRED
        );
    }
}
