package com.reserix.api.screen.controller;

import com.reserix.api.screen.dto.RoomCreateRequest;
import com.reserix.api.screen.dto.RoomResponse;
import com.reserix.api.screen.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public RoomResponse createRoom(
            @Valid @RequestBody RoomCreateRequest request
            ) {
        return roomService.createRoom(request);
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(
            @PathVariable Long roomId
    ) {
        return roomService.getRoom(roomId);
    }

    @GetMapping
    public List<RoomResponse> getRooms() {
        return roomService.getRooms();
    }
}
