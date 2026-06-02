package com.reserix.api.reservation.websocket;

import com.reserix.api.reservation.entity.ReservationSeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(
            Long screeningId,
            List<Long> seatId,
            String seatCode,
            ReservationSeatStatus type,
            Long userId
    ) {
        SeatStatusEvent event = new SeatStatusEvent(
                screeningId,
                seatId,
                seatCode,
                type,
                userId,
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend(
                "/topic/screenings/" + screeningId + "/seats",
                event
        );
    }
}