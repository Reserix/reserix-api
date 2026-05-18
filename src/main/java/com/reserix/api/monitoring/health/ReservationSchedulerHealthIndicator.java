package com.reserix.api.monitoring.health;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ReservationSchedulerHealthIndicator implements HealthIndicator {

    private volatile LocalDateTime lastExecutionTime;

    public void markExecuted() {
        lastExecutionTime = LocalDateTime.now();
    }

    @Override
    public @Nullable Health health() {
        if (lastExecutionTime == null) {
            return Health.down()
                    .withDetail("reason", "Scheduler never executed")
                    .build();
        }

        Duration diff = Duration.between(lastExecutionTime, LocalDateTime.now());

        if (diff.toMinutes() > 5) {
            return Health.down()
                    .withDetail("reason", "Scheduler stalled")
                    .withDetail("lastExecution", lastExecutionTime)
                    .build();
        }

        return Health.up()
                .withDetail("lastExecution", lastExecutionTime)
                .build();
    }
}
