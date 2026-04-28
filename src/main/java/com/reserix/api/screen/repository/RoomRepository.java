package com.reserix.api.screen.repository;

import com.reserix.api.screen.entity.Room;
import com.reserix.api.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsRoomByTheaterAndName(Theater theater, String name);
}
