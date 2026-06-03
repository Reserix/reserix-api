package com.reserix.api.screen.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.common.response.PageResponse;
import com.reserix.api.screen.dto.RoomCreateRequest;
import com.reserix.api.screen.dto.RoomResponse;
import com.reserix.api.screen.dto.RoomSeatsResponse;
import com.reserix.api.screen.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody RoomCreateRequest request
            ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created", roomService.createRoom(request)));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoom(
            @PathVariable Long roomId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(roomService.getRoom(roomId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> getRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(roomService.getRooms(page, size)));
    }

    @GetMapping("/{roomId}/seats")
    public ResponseEntity<ApiResponse<RoomSeatsResponse>> getRoomSeats(
        @PathVariable Long roomId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(roomService.getRoomSeats(roomId)));
    }
}
