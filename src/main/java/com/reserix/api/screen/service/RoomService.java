package com.reserix.api.screen.service;

import com.reserix.api.common.response.PageResponse;
import com.reserix.api.screen.dto.RoomCreateRequest;
import com.reserix.api.screen.dto.RoomResponse;
import com.reserix.api.screen.entity.Room;
import com.reserix.api.screen.entity.Seat;
import com.reserix.api.screen.entity.SeatType;
import com.reserix.api.screen.repository.RoomRepository;
import com.reserix.api.screen.repository.SeatRepository;
import com.reserix.api.theater.entity.Theater;
import com.reserix.api.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;

    @Transactional(rollbackFor = Exception.class)
    public RoomResponse createRoom(RoomCreateRequest request) {
        Theater theater = theaterRepository.findById(request.theaterId())
                .orElseThrow(() -> new IllegalArgumentException("Theater not found"));

        if (roomRepository.existsRoomByTheaterAndName(theater, request.name())) {
            throw new IllegalArgumentException("Room already exists at this theater");
        }

        try {
            Room room = new Room(
                    theater,
                    request.name(),
                    request.rowCount(),
                    request.columnCount()
            );

            Room savedRoom = roomRepository.save(room);

            List<Seat> seats = request.seats()
                    .stream()
                    .map(seatRequest -> new Seat(
                            savedRoom,
                            seatRequest.rowNumber(),
                            seatRequest.columnNumber(),
                            seatRequest.rowLabel(),
                            seatRequest.seatNumber(),
                            seatRequest.seatType() == null ? SeatType.STANDARD : seatRequest.seatType(),
                            seatRequest.active() == null ? true : seatRequest.active(),
                            1
                    ))
                    .toList();

            seatRepository.saveAll(seats);

            return RoomResponse.from(savedRoom, (long) seats.size());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Room already exists at this theater");
        }
    }

    public RoomResponse getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        long seatCount = seatRepository.countByRoomId(room.getId());

        return RoomResponse.from(room, seatCount);
    }

    // TODO: This part should be optimized using GroupBy
    public PageResponse<RoomResponse> getRooms(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<Room> roomPage = roomRepository.findAll(pageable);

        List<RoomResponse> content = roomPage.getContent()
                .stream()
                .map(room -> {
                    long seatCount = seatRepository.countByRoomId(room.getId());
                    return RoomResponse.from(room, seatCount);
                })
                .toList();

        return new PageResponse<>(
                content,
                roomPage.getNumber(),
                roomPage.getSize(),
                roomPage.getTotalElements(),
                roomPage.getTotalPages()
        );
    }
}
