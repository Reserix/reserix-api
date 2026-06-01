package com.reserix.api.theater.dto;

import com.reserix.api.theater.entity.Theater;
import com.reserix.api.theater.entity.TheaterThumbnail;
import com.reserix.api.user.entity.User;
import com.reserix.api.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public record TheaterResponse(
        Long id,
        String name,
        String address,
        String description,
        String email,
        String phoneNumber,
        Double latitude,
        Double longitude,
        String ownerEmail,
        String ownerName,
        UserRole ownerRole,
        List<String> thumbnailUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TheaterResponse from(Theater theater) {

        return new TheaterResponse(
                theater.getId(),
                theater.getName(),
                theater.getAddress(),
                theater.getDescription(),
                theater.getEmail(),
                theater.getPhoneNumber(),
                theater.getLatitude(),
                theater.getLongitude(),
                theater.getOwner().getEmail(),
                theater.getOwner().getUsername(),
                theater.getOwner().getRole(),
                theater.getThumbnails()
                        .stream()
                        .map(TheaterThumbnail::getFilePath)
                        .toList(),
                theater.getCreatedAt(),
                theater.getUpdatedAt()
        );
    }
}
