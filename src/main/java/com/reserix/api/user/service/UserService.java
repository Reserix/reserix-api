package com.reserix.api.user.service;

import com.reserix.api.common.response.PageResponse;
import com.reserix.api.user.dto.UserCreateByAdminRequest;
import com.reserix.api.user.dto.UserResponse;
import com.reserix.api.user.entity.User;
import com.reserix.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateByAdminRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                request.username(),
                encodedPassword,
                request.role()
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserResponse.from(user);
    }

    public PageResponse<UserResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(UserResponse::from)
                .toList();

        return new PageResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }
}
