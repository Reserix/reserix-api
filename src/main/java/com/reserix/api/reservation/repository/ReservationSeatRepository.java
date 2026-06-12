package com.reserix.api.reservation.repository;

import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.entity.ReservationSeatStatus;
import com.reserix.api.screen.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    List<ReservationSeat> findByReservationId(Long reservationId);

    List<ReservationSeat> findReservationSeatsByScreeningAndStatusIn(Screening screening, Collection<ReservationSeatStatus> statuses);

    boolean existsByScreeningIdAndSeatIdAndStatusIn(
            Long screeningId,
            Long seatId,
            Collection<ReservationSeatStatus> statuses
    );

    @Query("""
        select rs.seat.id
        from ReservationSeat rs
        where rs.screening.id = :screeningId
          and rs.seat.id in :seatIds
    """)
    List<Long> findReservedSeatIds(
            @Param("screeningId") Long screeningId,
            @Param("seatIds") List<Long> seatIds
    );

    @Modifying
    @Query("""
        update ReservationSeat rs
        set rs.status = :releasedStatus
        where rs.reservation.id in :reservationIds
          and rs.status = :pendingStatus
    """)
    int bulkReleaseReservationSeats(
            @Param("reservationIds") List<Long> reservationIds,
            @Param("pendingStatus") ReservationSeatStatus pendingStatus,
            @Param("releasedStatus") ReservationSeatStatus releasedStatus
    );

    @Query("""
    select rs.seat.id
    from ReservationSeat rs
    where rs.screening.id = :screeningId
      and rs.seat.id in :seatIds
      and rs.status in :statuses
""")
    List<Long> findUnavailableSeatIds(
            Long screeningId,
            List<Long> seatIds,
            List<ReservationSeatStatus> statuses
    );
}
