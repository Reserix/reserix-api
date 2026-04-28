package com.reserix.api.screen.repository;

import com.reserix.api.screen.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    long countByRoomId(Long roomId);
}
