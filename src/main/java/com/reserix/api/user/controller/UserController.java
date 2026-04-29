package com.reserix.api.user.controller;

import com.reserix.api.user.dto.UserCreateRequest;
import com.reserix.api.user.dto.UserResponse;
import com.reserix.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody UserCreateRequest request
            ) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(
            @PathVariable Long userId
    ) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }
}
