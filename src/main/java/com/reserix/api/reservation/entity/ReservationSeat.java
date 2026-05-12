package com.reserix.api.reservation.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.screen.entity.Screening;
import com.reserix.api.screen.entity.Seat;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "reservation_seats"
)
public class ReservationSeat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: reservation_seats.reservation_id -> reservations.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumns({
//            @JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false),
//            @JoinColumn(name = "screening_id", referencedColumnName = "screening_id", insertable = false, updatable = false, nullable = false)
//    })
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // FK: reservation_seats.screening_id -> screenings.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    // FK: reservation_seats.seat_id -> seats.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationSeatStatus status;

    protected ReservationSeat() {

    }

    public ReservationSeat(Reservation reservation, Seat seat, Integer price) {
        this.reservation = reservation;
        this.screening = reservation.getScreening();
        this.seat = seat;
        this.price = price;
        this.status = ReservationSeatStatus.PENDING;
    }
}
