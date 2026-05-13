package com.reserix.api.payment.repository;

import com.reserix.api.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}
