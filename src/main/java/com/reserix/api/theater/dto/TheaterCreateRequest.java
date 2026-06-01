package com.reserix.api.theater.dto;

import com.reserix.api.theater.entity.TheaterStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TheaterCreateRequest(

        @NotBlank(message = "Theater name is required")
        @Size(max = 255, message = "Theater name must be less than 255 characters")
        String name,

        @NotBlank(message = "Theater address is required")
        @Size(max = 500, message = "Theater address must be less than 500 characters")
        String address,

        @NotNull(message = "Owner ID is required")
        Long ownerId,

        @Size(max = 2000, message = "Description must be less than 2000 characters")
        String description,

        @Pattern(
                regexp = "^[0-9+\\-() ]*$",
                message = "Invalid phone number format"
        )
        String phoneNumber,

        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must be less than 255 characters")
        String email,

        @DecimalMin(
                value = "-90.0",
                message = "Latitude must be greater than or equal to -90"
        )
        @DecimalMax(
                value = "90.0",
                message = "Latitude must be less than or equal to 90"
        )
        Double latitude,

        @DecimalMin(
                value = "-180.0",
                message = "Longitude must be greater than or equal to -180"
        )
        @DecimalMax(
                value = "180.0",
                message = "Longitude must be less than or equal to 180"
        )
        Double longitude,

        @NotNull(message = "Status is required")
        TheaterStatus status

) {
}