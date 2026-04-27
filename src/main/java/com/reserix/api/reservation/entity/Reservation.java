package com.reserix.api.reservation.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.screen.entity.Screening;
import com.reserix.api.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "reservations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id", "screening_id"})
        }
)
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: reservations.user_id -> users.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // FK: reservations.screening_id -> screenings.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    protected Reservation() {

    }

    public Reservation(User user, Screening screening, LocalDateTime expireAt) {
        this.user = user;
        this.screening = screening;
        this.status = ReservationStatus.PENDING;
        this.expireAt = expireAt;
    }
}
