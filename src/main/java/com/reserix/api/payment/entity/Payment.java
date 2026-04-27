package com.reserix.api.payment.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"reservation_id"})
        }
)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    int user_id FK
        int reservation_id FK
        int status
     */
    // FK: payments.user_id -> users.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // FK: payments.reservation_id -> reservations.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    protected Payment() {

    }

    public Payment(User user, Reservation reservation) {
        this.user = user;
        this.reservation = reservation;
        this.status = PaymentStatus.PENDING;
    }
}
