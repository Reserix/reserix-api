package com.reserix.api.reservation.scheduler;

import com.reserix.api.reservation.service.ReservationExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpireScheduler {
    private static final String LOCK_KEY = "lock:reservation-expire-scheduler";
    private static final Duration LOCK_TTL = Duration.ofSeconds(25);

    private final ReservationExpirationService reservationExpirationService;
    private final StringRedisTemplate stringRedisTemplate;

    @Scheduled(fixedDelay = 30000)
    public void expirePendingReservations() {
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(LOCK_KEY, "locked", LOCK_TTL);

        if (!Boolean.TRUE.equals(locked)) {
            return;
        }

        try {
            int expiredCount = reservationExpirationService.expirePendingReservations();

            if (expiredCount > 0) {
                log.info("Expired pending reservations count={}", expiredCount);
            }
        } catch (Exception e) {
            log.error("Failed to expire pending reservations", e);
        } finally {
            stringRedisTemplate.delete(LOCK_KEY);
        }
    }
}
