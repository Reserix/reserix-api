package com.reserix.api.payment.service;

import com.reserix.api.common.exception.BusinessException;
import com.reserix.api.common.exception.ErrorCode;
import com.reserix.api.payment.dto.MockPaymentIntentRequest;
import com.reserix.api.payment.dto.MockPaymentIntentResponse;
import com.reserix.api.payment.dto.MockPaymentResult;
import com.reserix.api.payment.dto.MockPaymentWebhookRequest;
import com.reserix.api.payment.entity.Payment;
import com.reserix.api.payment.entity.PaymentStatus;
import com.reserix.api.payment.repository.PaymentRepository;
import com.reserix.api.reservation.entity.Reservation;
import com.reserix.api.reservation.entity.ReservationSeat;
import com.reserix.api.reservation.entity.ReservationStatus;
import com.reserix.api.reservation.repository.ReservationRepository;
import com.reserix.api.reservation.repository.ReservationSeatRepository;
import com.reserix.api.screen.entity.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MockPaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Transactional
    public MockPaymentIntentResponse createIntent(MockPaymentIntentRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "Reservation not found"));

        validatePayableReservation(reservation);

        Payment payment = paymentRepository.findByReservationId(reservation.getId())
                .orElseGet(() -> {
                    Payment newPayment = Payment.createMockPending(reservation, calculateAmount(reservation));
                    Payment savedPayment = paymentRepository.save(newPayment);
                    savedPayment.assignProviderPaymentId("mock_payment_" + savedPayment.getId());
                    return savedPayment;
                });

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not pending");
        }

        return MockPaymentIntentResponse.from(payment);
    }

    @Transactional
    public void handleWebhook(MockPaymentWebhookRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return;
        }

        Reservation reservation = payment.getReservation();

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            payment.cancel();
            return;
        }

        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findByReservationId(reservation.getId());

        if (reservationSeats.isEmpty()) {
            payment.fail();
            reservation.cancel();
            throw new BusinessException(ErrorCode.CONFLICT, "Reservation has no seats");
        }

        if (reservation.getExpireAt().isBefore(LocalDateTime.now())) {
            payment.fail();
            reservation.expire();
            reservationSeats.forEach(ReservationSeat::release);
            return;
        }

        if (request.result() == MockPaymentResult.SUCCESS) {
            payment.succeed();
            reservation.confirm();
            reservationSeats.forEach(ReservationSeat::confirm);
            return;
        }

        payment.fail();
        reservation.cancel();
        reservationSeats.forEach(ReservationSeat::release);
    }

    private void validatePayableReservation(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Reservation is not pending");
        }

        if (reservation.getExpireAt().isBefore(LocalDateTime.now())) {
            reservation.expire();
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Reservation has expired");
        }
    }

    private BigDecimal calculateAmount(Reservation reservation) {
        Integer total = 0;

        List<ReservationSeat> seats = reservationSeatRepository.findByReservationId(reservation.getId());

        for (ReservationSeat seat : seats) {
            total +=  seat.getPrice();
        }
        return BigDecimal.valueOf(total);
    }
}
