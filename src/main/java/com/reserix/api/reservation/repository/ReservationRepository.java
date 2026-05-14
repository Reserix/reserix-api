package com.reserix.api.reservation.repository;

import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
        select r.id
        from Reservation r
        where r.status = :status
          and r.expireAt < :now
        order by r.expireAt asc
    """)
    List<Long> findExpiredPendingReservationIds(
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Modifying
    @Query("""
        update Reservation r
        set r.status = :expiredStatus
        where r.id in :ids
          and r.status = :pendingStatus
    """)
    int bulkExpireReservations(
            @Param("ids") List<Long> ids,
            @Param("pendingStatus") ReservationStatus pendingStatus,
            @Param("expiredStatus") ReservationStatus expiredStatus
    );
}
