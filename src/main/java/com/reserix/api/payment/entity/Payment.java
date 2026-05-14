package com.reserix.api.payment.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

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

    // FK: payments.reservation_id -> reservations.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentProvider provider;

    @Column(name = "provider_payment_id", length = 100)
    private String providerPaymentId;

    protected Payment() {

    }

    public static Payment createMockPending(Reservation reservation, BigDecimal amount) {
        Payment payment = new Payment();
        payment.reservation = reservation;
        payment.amount = amount;
        payment.status = PaymentStatus.PENDING;
        payment.provider = PaymentProvider.MOCK;
        return payment;
    }

    public void assignProviderPaymentId(String providerPaymentId) {
        this.providerPaymentId = providerPaymentId;
    }

    public void succeed() {
        if (this.status != PaymentStatus.PENDING) {
            return;
        }

        this.status = PaymentStatus.SUCCESS;
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            return;
        }

        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        if (this.status != PaymentStatus.PENDING) {
            return;
        }

        this.status = PaymentStatus.CANCELED;
    }
}
