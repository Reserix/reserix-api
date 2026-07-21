package com.reserix.api.chat.dto;

import com.reserix.api.chat.enums.ChatSessionStatus;

public record ChatSessionCloseResponse(
        Long sessionId,
        ChatSessionStatus status
) {
}
