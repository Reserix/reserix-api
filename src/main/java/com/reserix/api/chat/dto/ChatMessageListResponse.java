package com.reserix.api.chat.dto;

import java.util.List;

public record ChatMessageListResponse(
        Long sessionId,
        List<ChatMessageResponse> messages
) {
}
