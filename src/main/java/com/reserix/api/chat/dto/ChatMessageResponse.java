package com.reserix.api.chat.dto;

import com.reserix.api.chat.enums.ChatIntent;
import com.reserix.api.chat.enums.ChatRole;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
        Long sessionId,
        Long messageId,
        ChatRole role,
        String content,
        ChatIntent intent,
        List<ChatToolCallDto> toolCalls,
        LocalDateTime createdAt
) {
}
