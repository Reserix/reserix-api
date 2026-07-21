package com.reserix.api.chat.dto;

import com.reserix.api.chat.enums.ChatSessionStatus;

import java.time.LocalDateTime;

public record ChatSessionCreateResponse(
        Long sessionId,
        ChatSessionStatus status,
        LocalDateTime createdAt
) {
}
