package com.reserix.api.chat.dto;

import com.reserix.api.chat.enums.ChatIntent;

public record IntentDetectionResult(
        ChatIntent intent,
        double confidence
) {
}
