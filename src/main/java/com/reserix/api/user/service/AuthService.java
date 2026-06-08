package com.reserix.api.user.service;

import com.reserix.api.common.exception.BusinessException;
import com.reserix.api.common.exception.ErrorCode;
import com.reserix.api.security.JwtTokenProvider;
import com.reserix.api.user.dto.LoginRequest;
import com.reserix.api.user.dto.LoginResponse;
import com.reserix.api.user.dto.UserCreateRequest;
import com.reserix.api.user.dto.UserResponse;
import com.reserix.api.user.entity.User;
import com.reserix.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Invalid email or password");
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(accessToken, "Bearer", user.getRole());
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.CONFLICT, "This email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                request.username(),
                encodedPassword
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
}
