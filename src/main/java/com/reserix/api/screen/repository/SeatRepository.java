package com.reserix.api.screen.repository;

import com.reserix.api.screen.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    long countByRoomId(Long roomId);
    List<Seat> findSeatsByRoomId(Long roomId);
}
