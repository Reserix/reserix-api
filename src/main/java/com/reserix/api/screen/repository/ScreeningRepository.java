package com.reserix.api.screen.repository;

import com.reserix.api.screen.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    boolean existsByRoomIdAndStartTime(Long roomId, LocalDateTime startTime);
}
