package com.reserix.api.user.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.common.response.PageResponse;
import com.reserix.api.user.dto.UserCreateByAdminRequest;
import com.reserix.api.user.dto.UserCreateRequest;
import com.reserix.api.user.dto.UserResponse;
import com.reserix.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateByAdminRequest request
            ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created", userService.createUser(request)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(userService.getUser(userId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .ok(ApiResponse.success(userService.getAll(page, size)));
    }
}
