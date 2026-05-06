package com.reserix.api.user.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.user.dto.LoginRequest;
import com.reserix.api.user.dto.LoginResponse;
import com.reserix.api.user.dto.UserCreateRequest;
import com.reserix.api.user.dto.UserResponse;
import com.reserix.api.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
            ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserCreateRequest request
            ) {
        UserResponse response = authService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created", response));
    }
}
