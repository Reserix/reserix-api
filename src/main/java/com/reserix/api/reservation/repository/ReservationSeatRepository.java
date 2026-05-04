package com.reserix.api.reservation.repository;

import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.screen.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findReservationSeatsByScreening(Screening screening);

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
}
