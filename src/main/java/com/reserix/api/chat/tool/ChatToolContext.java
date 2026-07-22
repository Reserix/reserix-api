package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatIntent;

public record ChatToolContext(
        Long sessionId,
        Long userId,
        ChatIntent intent,
        String rawUserMessage
) {
}
