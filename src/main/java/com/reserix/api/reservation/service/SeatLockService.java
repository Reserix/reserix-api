package com.reserix.api.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatLockService {
    private final StringRedisTemplate redisTemplate;

    private static final Duration LOCK_TTL = Duration.ofMinutes(5);

    public boolean lockSeat(Long screeningId, Long seatId, Long userId) {
        String key = buildSeatLockKey(screeningId, seatId);
        String value = String.valueOf(userId);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, value, LOCK_TTL);

        return Boolean.TRUE.equals(success);
    }

    public void lockSeats(Long screeningId, List<Long> seatIds, Long userId) {
        List<Long> lockedSeatIds = new ArrayList<>();

        try {
            for (Long seatId : seatIds) {
                boolean locked = lockSeat(screeningId, seatId, userId);

                if (!locked) {
                    throw new IllegalArgumentException("Seat already locked: " + seatId);
                }

                lockedSeatIds.add(seatId);
            }
        } catch (Exception e) {
            for (Long lockedSeatId : lockedSeatIds) {
                unlockSeat(screeningId, lockedSeatId, userId);
            }
            throw e;
        }
    }

    public void unlockSeat(Long screeningId, Long seatId, Long userId) {
        String key = buildSeatLockKey(screeningId, seatId);
        String value = redisTemplate.opsForValue().get(key);

        if (String.valueOf(userId).equals(value)) {
            redisTemplate.delete(key);
        }
    }

    public boolean isLocked(Long screeningId, Long seatId) {
        String key = buildSeatLockKey(screeningId, seatId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String buildSeatLockKey(Long screeningId, Long seatId) {
        return "reservation:lock:screening:" + screeningId + ":seat:" + seatId;
    }
}
